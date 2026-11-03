package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import io.github.chamagalhaes.web2petshop.repository.IAgendamentoRepository;
import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoServiceImpl implements IAgendamentoService {

    private final IAgendamentoRepository agendamentoRepository;

    public AgendamentoServiceImpl(IAgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    public List<Agendamento> getAllAgendamentos() {
        return agendamentoRepository.findAll();
    }

    @Override
    public Optional<Agendamento> getAgendamentoById(Long id) {
        return agendamentoRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveAgendamento(Agendamento agendamento) {
        if (agendamento.getServicos() != null) {
            for (AgendamentoServico servico : agendamento.getServicos()) {
                servico.setAgendamento(agendamento);
            }
        }

        agendamento.calculateTotal();
        agendamentoRepository.save(agendamento);
    }

    @Override
    public void deleteAgendamentoById(Long id) {
        agendamentoRepository.deleteById(id);
    }
}
