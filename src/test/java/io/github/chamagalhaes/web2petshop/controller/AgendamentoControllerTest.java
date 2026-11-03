package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.config.SecurityConfig;
import io.github.chamagalhaes.web2petshop.config.TestConfig;
import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;
import io.github.chamagalhaes.web2petshop.service.IPetService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IAgendamentoService agendamentoService;

    @Autowired
    private IPetService petService;

    @AfterEach
    void resetMocks() {
        reset(agendamentoService, petService);
    }

    private Pet criarPet() {
        Pet pet = new Pet();

        pet.setId(1L);
        pet.setNome("Pudin");
        pet.setRaca("Gato");
        pet.setIdade(2);
        pet.setTamanho("PEQUENO");

        return pet;
    }

    private Agendamento criarAgendamento() {
        Agendamento agendamento = new Agendamento();

        agendamento.setId(1L);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setStatus("PENDENTE");
        agendamento.setPet(criarPet());

        AgendamentoServico servico = new AgendamentoServico();
        servico.setNome("Banho");
        servico.setValor(new BigDecimal("50.00"));

        agendamento.addServico(servico);
        agendamento.calculateTotal();

        return agendamento;
    }

    @Test
    @DisplayName("GET /agendamento - Redireciona usuário não autenticado para login")
    void testIndexNotAuthenticatedUser() throws Exception {

        mockMvc.perform(get("/agendamento"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("GET /agendamento - Lista agendamentos para usuário autenticado")
    void testIndexAuthenticatedUser() throws Exception {

        when(agendamentoService.getAllAgendamentos())
                .thenReturn(List.of(criarAgendamento()));

        mockMvc.perform(get("/agendamento"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/index"))
                .andExpect(model().attributeExists("agendamentosList"))
                .andExpect(content().string(containsString("Pudin")));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("GET /agendamento/criar - ADMIN acessa formulário")
    void testCreateFormAuthorizedUser() throws Exception {

        when(petService.getAllPet()).thenReturn(List.of(criarPet()));

        mockMvc.perform(get("/agendamento/criar"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/form"))
                .andExpect(model().attributeExists("agendamento"))
                .andExpect(model().attributeExists("petsList"));
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("GET /agendamento/criar - USER não pode acessar formulário")
    void testCreateFormNotAuthorizedUser() throws Exception {

        mockMvc.perform(get("/agendamento/criar"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(petService);
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /agendamento/salvar - Falha na validação")
    void testSaveAgendamentoValidationError() throws Exception {

        Agendamento agendamento = new Agendamento();

        when(petService.getAllPet()).thenReturn(List.of(criarPet()));

        mockMvc.perform(post("/agendamento/salvar")
                        .with(csrf())
                        .flashAttr("agendamento", agendamento))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/form"))
                .andExpect(model().attributeHasErrors("agendamento"))
                .andExpect(model().attributeExists("petsList"));

        verify(agendamentoService, never())
                .saveAgendamento(any(Agendamento.class));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /agendamento/salvar - Salva agendamento válido")
    void testSaveValidAgendamento() throws Exception {

        Agendamento agendamento = criarAgendamento();

        mockMvc.perform(post("/agendamento/salvar")
                        .with(csrf())
                        .flashAttr("agendamento", agendamento))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento"));

        verify(agendamentoService).saveAgendamento(any(Agendamento.class));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /agendamento/salvar - Pet inexistente")
    void testSaveAgendamentoPetNotFound() throws Exception {

        Agendamento agendamento = criarAgendamento();

        when(agendamentoService.getAgendamentoById(anyLong()))
                .thenReturn(Optional.empty());

        doThrow(new EntityNotFoundException("Pet não encontrado."))
                .when(agendamentoService)
                .saveAgendamento(any(Agendamento.class));

        when(petService.getAllPet()).thenReturn(List.of(criarPet()));

        mockMvc.perform(post("/agendamento/salvar")
                        .with(csrf())
                        .flashAttr("agendamento", agendamento))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/form"))
                .andExpect(model().attribute("error", "Pet não encontrado."))
                .andExpect(model().attributeExists("petsList"));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /agendamento/salvar - Argumento inválido")
    void testSaveAgendamentoIllegalArgument() throws Exception {

        Agendamento agendamento = criarAgendamento();

        doThrow(new IllegalArgumentException("É necessário selecionar um pet."))
                .when(agendamentoService)
                .saveAgendamento(any(Agendamento.class));

        when(petService.getAllPet()).thenReturn(List.of(criarPet()));

        mockMvc.perform(post("/agendamento/salvar")
                        .with(csrf())
                        .flashAttr("agendamento", agendamento))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/form"))
                .andExpect(model().attribute("error",
                        "É necessário selecionar um pet."));
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("POST /agendamento/salvar - USER não pode cadastrar")
    void testSaveAgendamentoNotAuthorizedUser() throws Exception {

        mockMvc.perform(post("/agendamento/salvar")
                        .with(csrf())
                        .flashAttr("agendamento", criarAgendamento()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(agendamentoService);
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /agendamento/deletar/{id} - Exclui agendamento")
    void testDeleteAgendamento() throws Exception {

        mockMvc.perform(post("/agendamento/deletar/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agendamento"));

        verify(agendamentoService).deleteAgendamentoById(1L);
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("GET /agendamento/editar/{id} - Abre formulário de edição")
    void testEditAgendamento() throws Exception {

        when(agendamentoService.getAgendamentoById(1L))
                .thenReturn(Optional.of(criarAgendamento()));

        when(petService.getAllPet()).thenReturn(List.of(criarPet()));

        mockMvc.perform(get("/agendamento/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamento/form"))
                .andExpect(model().attributeExists("agendamento"))
                .andExpect(model().attributeExists("petsList"));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("GET /agendamento/editar/{id} - Agendamento não encontrado")
    void testEditAgendamentoNotFound() throws Exception {

        when(agendamentoService.getAgendamentoById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/agendamento/editar/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("GET /agendamento/editar/{id} - USER não pode editar")
    void testEditAgendamentoNotAuthorizedUser() throws Exception {

        mockMvc.perform(get("/agendamento/editar/1"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(agendamentoService);
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("POST /agendamento/deletar/{id} - USER não pode excluir")
    void testDeleteAgendamentoNotAuthorizedUser() throws Exception {

        mockMvc.perform(post("/agendamento/deletar/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(agendamentoService);
    }
}
