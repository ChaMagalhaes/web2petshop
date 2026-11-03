package io.github.chamagalhaes.web2petshop.impl;

import io.github.chamagalhaes.web2petshop.model.User;
import io.github.chamagalhaes.web2petshop.repository.IUserRepository;
import io.github.chamagalhaes.web2petshop.service.IUserService;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findUserByEmail(email);

        org.springframework.security.core.userdetails.User springUser = null;

        if(opt.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }else {
            User user = opt.get();
            List<String> roles = user.getRoles();
            Set<GrantedAuthority> ga = new HashSet<>();
            for(String role : roles) {
                ga.add(new SimpleGrantedAuthority(role));
            }

            springUser = new org.springframework.security.core.userdetails.User(
                    email,
                    user.getPassword(),
                    ga
            );
        }

        return springUser;
    }
}
