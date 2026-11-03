package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.IAgendamentoRepository;
import io.github.chamagalhaes.web2petshop.repository.IPetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceImplTest {

    @Mock
    private IAgendamentoRepository agendamentoRepository;

    @Mock
    private IPetRepository petRepository;

    @InjectMocks
    private AgendamentoServiceImpl agendamentoService;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
    }

    @Test
    void deveSalvarAgendamentoComPetExistente() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(pet);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        agendamentoService.saveAgendamento(agendamento);

        assertSame(pet, agendamento.getPet());
        verify(petRepository).findById(1L);
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void deveLancarExcecaoQuandoPetForNulo() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.saveAgendamento(agendamento)
        );

        verifyNoInteractions(petRepository);
        verifyNoInteractions(agendamentoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoPetNaoPossuirId() {
        Pet petSemId = new Pet();

        Agendamento agendamento = new Agendamento();
        agendamento.setPet(petSemId);

        assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.saveAgendamento(agendamento)
        );

        verifyNoInteractions(petRepository);
        verifyNoInteractions(agendamentoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoPetNaoExistir() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(pet);

        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> agendamentoService.saveAgendamento(agendamento)
        );

        verify(petRepository).findById(1L);
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    void deveAssociarServicosAoAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(pet);

        AgendamentoServico servico = new AgendamentoServico();
        servico.setNome("Banho");
        servico.setValor(new BigDecimal("50.00"));

        agendamento.setServicos(List.of(servico));

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        agendamentoService.saveAgendamento(agendamento);

        assertSame(agendamento, servico.getAgendamento());
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void deveCalcularTotalDosServicos() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(pet);

        AgendamentoServico banho = new AgendamentoServico();
        banho.setNome("Banho");
        banho.setValor(new BigDecimal("50.00"));

        AgendamentoServico tosa = new AgendamentoServico();
        tosa.setNome("Tosa");
        tosa.setValor(new BigDecimal("35.00"));

        agendamento.setServicos(List.of(banho, tosa));

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        agendamentoService.saveAgendamento(agendamento);

        assertEquals(
                0,
                new BigDecimal("85.00").compareTo(agendamento.getTotal())
        );

        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    void deveDefinirTotalZeroQuandoNaoHouverServicos() {
        Agendamento agendamento = new Agendamento();
        agendamento.setPet(pet);
        agendamento.setServicos(List.of());

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        agendamentoService.saveAgendamento(agendamento);

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(agendamento.getTotal())
        );

        verify(agendamentoRepository).save(agendamento);
    }
}
