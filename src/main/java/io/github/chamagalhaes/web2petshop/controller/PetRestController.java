package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.service.IPetService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Permite acesso do React
@RestController
@RequestMapping("api/pet")
public class PetRestController {

    private final IPetService petService;

    public PetRestController(IPetService petService) {
        this.petService = petService;
    }

    // Listar todos os pets
    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {

        List<Pet> pets = petService.getAllPet();

        return ResponseEntity.ok(pets);
    }

    // Buscar pet por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {

        try {
            Pet pet = petService.getPetById(id);

            return ResponseEntity.ok(pet);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cadastrar pet
    @PostMapping
    public ResponseEntity<?> createPet(@Valid @RequestBody Pet pet, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result.getAllErrors());
        }

        pet.setId(null);
        petService.savePet(pet);

        return ResponseEntity.status(201).body(pet);
    }

    // Atualizar pet
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @Valid @RequestBody Pet petDetails, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result.getAllErrors());
        }

        try {
            Pet petAtualizado = petService.updatePet(id, petDetails);

            return ResponseEntity.ok(petAtualizado);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Excluir pet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPet(@PathVariable Long id) {

        try {
            petService.getPetById(id);
            petService.deletePetById(id);

            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
