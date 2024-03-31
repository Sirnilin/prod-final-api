package com.example.prodolymp.service;

import com.example.prodolymp.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositories userRepositories;
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserDetails user =  userRepositories.findByLogin(login);

        if (user == null)
        {
            throw new UsernameNotFoundException("User not found with email: " + login);
        }

        return user;
    }
}
