
package io.github.chamagalhaes.web2petshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(
                        "/register",
                        "/saveUser",
                        "/accessDenied",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()

                // Apenas ADMIN pode excluir pets
                .requestMatchers(
                        "/pet/criar",
                        "/pet/salvar",
                        "/pet/editar/**",
                        "/pet/atualizar",
                        "/pet/deletar/**"
                ).hasAuthority("ADMIN")

                // Demais rotas exigem autenticação
                .anyRequest().authenticated()
        );

        http.formLogin(login -> login
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
