package com.example.ticketbookingsystem.security;

import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link UserDetailsService} for loading user-specific data.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user by username.
     *
     * @param username the username identifying the user whose data is required.
     * @return the {@link UserDetails} of the user.
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found.");
        }

        User currentUser = userOptional.get();
        return org.springframework.security.core.userdetails.User.withUsername(username)
                .password(currentUser.getPassword())
                .roles(currentUser.getRole().toString())
                .build();
    }

}
