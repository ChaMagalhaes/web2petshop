package io.github.chamagalhaes.web2petshop.repository;

import io.github.chamagalhaes.web2petshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
