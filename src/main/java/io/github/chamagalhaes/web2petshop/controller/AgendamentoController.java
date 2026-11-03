package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.Agendamento;
import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;
import io.github.chamagalhaes.web2petshop.service.IPetService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/agendamento")
public class AgendamentoController {

    private final IAgendamentoService agendamentoService;

    private final IPetService petService;

    public AgendamentoController(IAgendamentoService agendamentoService, IPetService petService) {

        this.agendamentoService = agendamentoService;

        this.petService = petService;
    }

    @GetMapping
    public String index(Model model) {model.addAttribute("agendamentosList", agendamentoService.getAllAgendamentos());

        return "agendamento/index";
    }

    @GetMapping("/criar")
    public String criarAgendamento(Model model) {

        model.addAttribute("agendamento", new Agendamento());

        model.addAttribute("petsList", petService.getAllPet());

        return "agendamento/form";
    }

    @PostMapping("/salvar")
    public String salvarAgendamento(@ModelAttribute("agendamento") @Valid Agendamento agendamento, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("petsList", petService.getAllPet());

            return "agendamento/form";
        }

        agendamentoService.saveAgendamento(agendamento);

        return "redirect:/agendamento";
    }

    @PostMapping("/deletar/{id}")
    public String deletarAgendamento(@PathVariable Long id) {

        agendamentoService.deleteAgendamentoById(id);

        return "redirect:/agendamento";
    }

    @GetMapping("/editar/{id}")
    public String editarAgendamento(@PathVariable Long id, Model model) {

        Agendamento agendamento = agendamentoService.getAgendamentoById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agendamento não encontrado"
                ));

        model.addAttribute("agendamento", agendamento);

        model.addAttribute("petsList", petService.getAllPet());

        return "agendamento/form";
    }
}