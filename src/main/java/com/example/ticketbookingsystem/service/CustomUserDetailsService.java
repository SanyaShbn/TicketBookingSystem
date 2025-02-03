package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of the UserDetailsService interface for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email.
     *
     * @param email The email of the user.
     * @return The UserDetails of the user.
     * @throws UsernameNotFoundException If no user is found with the given email.
     */
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(), user.get().getPassword(), enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, getAuthority(user.get().getRole()));
    }

    private static List<GrantedAuthority> getAuthority (Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
}