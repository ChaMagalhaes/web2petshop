package io.github.chamagalhaes.web2petshop.repository;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAgendamentoRepository extends JpaRepository<Agendamento, Long> {
}
