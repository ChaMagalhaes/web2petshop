package io.github.chamagalhaes.web2petshop.repository;

import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAgendamentoServicoRepository extends JpaRepository<AgendamentoServico, Long> {
}
