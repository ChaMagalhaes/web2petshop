package io.github.chamagalhaes.web2petshop.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.IPetRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPetRepository petRepository;

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testSavePetIntegration() throws Exception {

        Pet pet = new Pet();
        pet.setNome("Luna");
        pet.setRaca("Gato");
        pet.setIdade(1);
        pet.setTamanho("PEQUENO");

        mockMvc.perform(post("/pet/salvar")
                        .with(csrf())
                        .flashAttr("pet", pet))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pet"));

        // Verifica se o pet foi salvo no banco
        assertTrue(petRepository.findAll()
                .stream()
                .anyMatch(p -> "Luna".equals(p.getNome())));
    }
}
