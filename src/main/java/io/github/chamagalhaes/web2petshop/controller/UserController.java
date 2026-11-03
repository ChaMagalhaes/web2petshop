package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.User;
import io.github.chamagalhaes.web2petshop.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register() {
        return "user/registerUser";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute  User user, Model model) {
        Long id = userService.saveUser(user);
        String message = "Usuário '" + id + "' cadastrado com sucesso!";
        model.addAttribute("msg", message);
        return "user/registerUser";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }


    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "user/accessDeniedPage";
    }
}
