
package io.github.chamagalhaes.web2petshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Segurança da API REST
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {

        http
                .securityMatcher("/api/**")

                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasAuthority("ADMIN")
                )

                .csrf(csrf -> csrf.disable())

                .httpBasic(basic -> {})

                .authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                // Acesso público
                .requestMatchers(
                        "/register",
                        "/saveUser",
                        "/login",
                        "/accessDenied",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()

                // Operações administrativas de Pets
                .requestMatchers(
                        "/pet/criar",
                        "/pet/salvar",
                        "/pet/editar/**",
                        "/pet/deletar/**"
                ).hasAuthority("ADMIN")

                // Operações administrativas de Agendamentos
                .requestMatchers(
                        "/agendamento/criar",
                        "/agendamento/salvar",
                        "/agendamento/editar/**",
                        "/agendamento/deletar/**"
                ).hasAuthority("ADMIN")

                // Demais rotas exigem autenticação
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/pet", true)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
        );

        http.exceptionHandling(handling -> handling
                .accessDeniedPage("/accessDenied")
        );

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}
