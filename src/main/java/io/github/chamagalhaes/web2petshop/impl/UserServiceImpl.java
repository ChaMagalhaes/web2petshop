package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.User;
import io.github.chamagalhaes.web2petshop.repository.IUserRepository;
import io.github.chamagalhaes.web2petshop.service.IUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    private final IUserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long saveUser(User user) {

        user.setRoles(List.of("USER"));

        String password = user.getPassword();

        String encodedPassword = passwordEncoder.encode(password);

        user.setPassword(encodedPassword);

        user = userRepository.save(user);

        return user.getId();
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (String role : user.getRoles()) {

            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                authorities
        );
    }
}
