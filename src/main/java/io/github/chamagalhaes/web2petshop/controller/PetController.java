package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.repository.PetRepository;
import io.github.chamagalhaes.web2petshop.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping("/pet")
    public String index(Model model) {
        model.addAttribute("petsList", petService.getAllPet());
        return "pet/index";
    }

    @GetMapping("/pet/criar")
    public String criarPet(Model model) {
        model.addAttribute("pet", new Pet());
        return "pet/criar";
    }

    @PostMapping("/pet/salvar")
    public String salvarPet(@ModelAttribute("pet") Pet pet) {
        petService.savePet(pet);
        return "redirect:/pet";
    }
}
