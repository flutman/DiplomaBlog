package com.example.diploma.security;

import com.example.diploma.model.User;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        JwtUser jwtUser = JwtUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getName())
                .password(user.getPassword())
//                .authorities(user.getA)
//                .role(user.getRole())
                .build();

        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);

        return jwtUser;
    }
}
