package io.github.chamagalhaes.web2petshop.config;

import io.github.chamagalhaes.web2petshop.service.IAgendamentoService;
import io.github.chamagalhaes.web2petshop.service.IPetService;

import org.mockito.Mockito;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public IPetService petService() {
        return Mockito.mock(IPetService.class);
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Primary
    public IAgendamentoService agendamentoService() {
        return Mockito.mock(IAgendamentoService.class);
    }
}