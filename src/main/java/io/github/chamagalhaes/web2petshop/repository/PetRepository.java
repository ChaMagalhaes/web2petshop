package io.github.chamagalhaes.web2petshop.repository;

import io.github.chamagalhaes.web2petshop.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
