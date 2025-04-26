package com.example.application.util;

import com.example.application.entity.User;
import com.example.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> userOptional=userRepository.findByUsername(usernameOrEmail);
        if (userOptional.isEmpty()){
            userOptional=userRepository.findByEmail(usernameOrEmail);
        }
//        User user = userOptional.orElseThrow(() ->
//                new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)
//        );
//        return user;
        if (userOptional.isEmpty()) {
            SecurityContextHolder.clearContext();
            // Instead of 500 error, it will gracefully fail during login
            throw new UsernameNotFoundException("Invalid credentials. Please login again.");
        }

        return userOptional.get();
    }
}
