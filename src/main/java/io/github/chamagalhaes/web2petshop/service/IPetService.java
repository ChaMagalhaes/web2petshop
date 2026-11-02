package io.github.chamagalhaes.web2petshop.service;

import io.github.chamagalhaes.web2petshop.model.Pet;

import java.util.List;

public interface IPetService {
    List<Pet> getAllPet();
    void savePet(Pet pet);
    Pet getPetById(long id);
    void deletePetById(long id);
}
