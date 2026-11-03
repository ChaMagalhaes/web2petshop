package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.service.IPetService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pet")
public class PetController {

    private final IPetService petService;

    public PetController(IPetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("petsList", petService.getAllPet());
        return "pet/index";
    }

    @GetMapping("/criar")
    public String criarPet(Model model) {
        model.addAttribute("pet", new Pet());
        return "pet/form";
    }

    @PostMapping("/salvar")
    public String salvarPet(@ModelAttribute("pet") @Valid  Pet pet,
                            BindingResult result,
                            Model model) {

        if(result.hasErrors()) {
            model.addAttribute("pet", pet);
            return "pet/form";
        }

        petService.savePet(pet);
        return "redirect:/pet";
    }

    @PostMapping("/deletar/{id}")
    public String deletarPet(@PathVariable Long id) {
        petService.deletePetById(id);
        return "redirect:/pet";
    }

    @GetMapping("/editar/{id}")
    public String editarPet(@PathVariable("id") Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute("pet", pet);
        return "pet/form";
    }
}
