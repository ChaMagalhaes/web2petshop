package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.Pet;
import io.github.chamagalhaes.web2petshop.service.IPetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PetController {
    private final IPetService petService;

    public PetController(IPetService petService) {
        this.petService = petService;
    }

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
    public String salvarPet(@ModelAttribute("pet") @Valid  Pet pet,
                            BindingResult result,
                            Model model) {

        if(result.hasErrors()) {
            model.addAttribute("pet", pet);
            return "pet/criar";
        }

        petService.savePet(pet);
        return "redirect:/pet";
    }

    @PostMapping("/pet/deletar/{id}")
    public String deletarPet(@PathVariable Long id) {
        petService.deletePetById(id);
        return "redirect:/pet";
    }

    @GetMapping("/pet/editar/{id}")
    public String editarPet(@PathVariable("id") Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute("pet", pet);
        return "pet/editar";
    }
}
