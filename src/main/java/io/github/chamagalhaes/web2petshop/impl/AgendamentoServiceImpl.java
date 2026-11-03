package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.model.AgendamentoServico;
import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.IAgendamentoRepository;
import io.github.chamagalhaes.web2petshop.repository.IPetRepository;
import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoServiceImpl implements IAgendamentoService {

    private final IAgendamentoRepository agendamentoRepository;
    private final IPetRepository petRepository;

    public AgendamentoServiceImpl(IAgendamentoRepository agendamentoRepository, IPetRepository petRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
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

        // Verifica se o pet foi informado
        if (agendamento.getPet() == null || agendamento.getPet().getId() == null) {
            throw new IllegalArgumentException("É necessário selecionar um pet.");
        }

        // Busca o pet no banco de dados
        Pet pet = petRepository.findById(agendamento.getPet().getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Pet não encontrado.")
                );

        // Associa o pet encontrado ao agendamento
        agendamento.setPet(pet);

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
