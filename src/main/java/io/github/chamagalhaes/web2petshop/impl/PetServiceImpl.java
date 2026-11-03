package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.IPetRepository;
import io.github.chamagalhaes.web2petshop.service.IPetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements IPetService {

    private final IPetRepository petRepository;

    public PetServiceImpl(IPetRepository petRepository) {

        this.petRepository = petRepository;
    }

    @Override
    public List<Pet> getAllPet() {

        return petRepository.findAll();
    }

    @Override
    public void savePet(Pet pet) {

        this.petRepository.save(pet);
    }

    @Override
    public Pet getPetById(long id) {

        return petRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pet não encontrado: " + id));
    }

    @Override
    public Pet updatePet(long id, Pet petDetails) {

        Pet petExistente = getPetById(id);

        petExistente.setNome(petDetails.getNome());

        petExistente.setRaca(petDetails.getRaca());

        petExistente.setIdade(petDetails.getIdade());

        petExistente.setTamanho(petDetails.getTamanho());

        return petRepository.save(petExistente);
    }

    @Override
    public void deletePetById(long id) {

        this.petRepository.deleteById(id);
    }
}
