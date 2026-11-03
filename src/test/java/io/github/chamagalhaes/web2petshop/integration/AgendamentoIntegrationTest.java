
package io.github.chamagalhaes.web2petshop.integration;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.IAgendamentoRepository;
import io.github.chamagalhaes.web2petshop.repository.IPetRepository;
import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AgendamentoIntegrationTest {

    @Autowired
    private IAgendamentoService agendamentoService;

    @Autowired
    private IPetRepository petRepository;

    @Autowired
    private IAgendamentoRepository agendamentoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Cria e persiste um pet válido para os testes
    private Pet criarPet() {
        Pet pet = new Pet();
        pet.setNome("Rex");
        pet.setRaca("Labrador");
        pet.setIdade(3);
        pet.setTamanho("GRANDE");

        return petRepository.save(pet);
    }

    // Cria um serviço válido
    private AgendamentoServico criarServico(String nome, String valor) {
        AgendamentoServico servico = new AgendamentoServico();
        servico.setNome(nome);
        servico.setValor(new BigDecimal(valor));

        return servico;
    }

    // Cria um agendamento com pet e serviços
    private Agendamento criarAgendamento(Pet pet) {
        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setStatus("PENDENTE");
        agendamento.setPet(pet);

        agendamento.addServico(criarServico("Banho", "50.00"));
        agendamento.addServico(criarServico("Tosa", "30.00"));

        return agendamento;
    }

    @Test
    void deveSalvarAgendamentoComPetExistente() {
        Pet pet = criarPet();
        Agendamento agendamento = criarAgendamento(pet);

        agendamentoService.saveAgendamento(agendamento);

        entityManager.flush();
        entityManager.clear();

        Agendamento resultado = agendamentoRepository
                .findById(agendamento.getId())
                .orElseThrow();

        assertNotNull(resultado.getId());
        assertEquals("Rex", resultado.getPet().getNome());
        assertEquals("PENDENTE", resultado.getStatus());
    }

    @Test
    void devePersistirServicosAssociadosAoAgendamento() {
        Pet pet = criarPet();
        Agendamento agendamento = criarAgendamento(pet);

        agendamentoService.saveAgendamento(agendamento);

        entityManager.flush();
        entityManager.clear();

        Agendamento resultado = agendamentoRepository
                .findById(agendamento.getId())
                .orElseThrow();

        assertEquals(2, resultado.getServicos().size());
    }

    @Test
    void deveCalcularTotalDosServicosAutomaticamente() {
        Pet pet = criarPet();
        Agendamento agendamento = criarAgendamento(pet);

        agendamentoService.saveAgendamento(agendamento);

        entityManager.flush();
        entityManager.clear();

        Agendamento resultado = agendamentoRepository
                .findById(agendamento.getId())
                .orElseThrow();

        assertEquals(
                0,
                new BigDecimal("80.00").compareTo(resultado.getTotal())
        );
    }

    @Test
    void deveBuscarAgendamentoPorId() {
        Pet pet = criarPet();
        Agendamento agendamento = criarAgendamento(pet);

        agendamentoService.saveAgendamento(agendamento);

        Optional<Agendamento> resultado =
                agendamentoService.getAgendamentoById(agendamento.getId());

        assertTrue(resultado.isPresent());
        assertEquals(agendamento.getId(), resultado.get().getId());
    }

    @Test
    void deveListarAgendamentos() {
        Pet pet = criarPet();

        Agendamento primeiro = criarAgendamento(pet);
        Agendamento segundo = criarAgendamento(pet);

        agendamentoService.saveAgendamento(primeiro);
        agendamentoService.saveAgendamento(segundo);

        assertEquals(2, agendamentoService.getAllAgendamentos().size());
    }

    @Test
    void deveExcluirAgendamentoESeusServicos() {
        Pet pet = criarPet();
        Agendamento agendamento = criarAgendamento(pet);

        agendamentoService.saveAgendamento(agendamento);

        Long id = agendamento.getId();

        entityManager.flush();
        entityManager.clear();

        agendamentoService.deleteAgendamentoById(id);

        entityManager.flush();

        assertFalse(agendamentoRepository.findById(id).isPresent());

        Long quantidadeServicos = entityManager.createQuery(
                "SELECT COUNT(s) FROM AgendamentoServico s " +
                        "WHERE s.agendamento.id = :id",
                Long.class
        ).setParameter("id", id).getSingleResult();

        assertEquals(0L, quantidadeServicos);
    }

    @Test
    void deveLancarExcecaoQuandoPetNaoExistir() {
        Pet petInexistente = new Pet();
        petInexistente.setId(-1L);

        Agendamento agendamento = criarAgendamento(petInexistente);

        assertThrows(
                EntityNotFoundException.class,
                () -> agendamentoService.saveAgendamento(agendamento)
        );
    }
}
