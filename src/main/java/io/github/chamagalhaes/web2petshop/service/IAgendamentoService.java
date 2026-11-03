package io.github.chamagalhaes.web2petshop.service;

import io.github.chamagalhaes.web2petshop.model.Agendamento;

import java.util.List;
import java.util.Optional;

public interface IAgendamentoService {
    List<Agendamento> getAllAgendamentos();
    Optional<Agendamento> getAgendamentoById(Long id);
    void saveAgendamento(Agendamento agendamento);
    void deleteAgendamentoById(Long id);
}
