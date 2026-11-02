package io.github.chamagalhaes.web2petshop.controller;

import io.github.chamagalhaes.web2petshop.model.User;
import io.github.chamagalhaes.web2petshop.repository.IUserRepository;
import io.github.chamagalhaes.web2petshop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute  User user, Model model) {
        Long id = userService.saveUser(user);
        String message = "User '" + id + "' saved successfully !";
        model.addAttribute("msg", message);
        return "user/registerUser";
    }

    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "user/accessDeniedPage";
    }
}
