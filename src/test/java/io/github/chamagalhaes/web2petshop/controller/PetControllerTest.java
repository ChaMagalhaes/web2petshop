package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.config.SecurityConfig;
import io.github.chamagalhaes.web2petshop.config.TestConfig;
import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.service.IPetService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;


@WebMvcTest(PetController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPetService petService;

    @AfterEach
    void resetMocks() {
        reset(petService);
    }

    private List<Pet> testCreatePetList() {
        Pet pet = new Pet();

        pet.setId(1L);
        pet.setNome("Pudin");
        pet.setRaca("Gato");
        pet.setIdade(2);
        pet.setTamanho("PEQUENO");

        return List.of(pet);
    }

    @Test
    @DisplayName("GET /pet - Redireciona usuário não autenticado para login")
    void testIndexNotAuthenticatedUser() throws Exception {

        mockMvc.perform(get("/pet"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /pet - Lista pets para usuário autenticado")
    void testIndexAuthenticatedUser() throws Exception {

        when(petService.getAllPet()).thenReturn(testCreatePetList());

        mockMvc.perform(get("/pet"))
                .andExpect(status().isOk())
                .andExpect(view().name("pet/index"))
                .andExpect(model().attributeExists("petsList"))
                .andExpect(content().string(containsString("Pudin")));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("GET /pet/criar - ADMIN acessa formulário de cadastro")
    void testCreateFormAuthorizedUser() throws Exception {

        mockMvc.perform(get("/pet/criar"))
                .andExpect(status().isOk())
                .andExpect(view().name("pet/criar"))
                .andExpect(model().attributeExists("pet"));
    }


    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("GET /pet/criar - USER não pode acessar cadastro")
    void testCreateFormNotAuthorizedUser() throws Exception {

        mockMvc.perform(get("/pet/criar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@web2petshop.com",
            authorities = {"USER"})
    @DisplayName("GET /pet - USER não visualiza botão de cadastrar")
    void testCreateButtonNotVisibleToUser() throws Exception {

        when(petService.getAllPet()).thenReturn(testCreatePetList());

        mockMvc.perform(get("/pet"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        not(containsString("Cadastrar Pet"))
                ));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /pet/salvar - Falha na validação")
    void testSavePetValidationError() throws Exception {

        Pet pet = new Pet();

        mockMvc.perform(post("/pet/salvar")
                        .with(csrf())
                        .flashAttr("pet", pet))
                .andExpect(status().isOk())
                .andExpect(view().name("pet/criar"))
                .andExpect(model().attributeHasErrors("pet"));

        verify(petService, never()).savePet(any(Pet.class));
    }

    @Test
    @WithMockUser(username = "admin@web2petshop.com",
            authorities = {"ADMIN"})
    @DisplayName("POST /pet/salvar - ADMIN cadastra pet válido")
    void testSaveValidPet() throws Exception {

        Pet pet = new Pet();

        pet.setNome("Novo Pet");
        pet.setRaca("Gato");
        pet.setIdade(2);
        pet.setTamanho("PEQUENO");

        mockMvc.perform(post("/pet/salvar")
                        .with(csrf())
                        .flashAttr("pet", pet))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pet"));

        verify(petService).savePet(any(Pet.class));
    }
}
