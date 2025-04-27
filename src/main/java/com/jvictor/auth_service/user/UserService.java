package com.jvictor.auth_service.user;

import com.jvictor.auth_service.confirmation_token.ConfirmationTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("this email does not exist"));
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("this user does not exist"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void checkIfEmailAlreadyRegistered(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new IllegalStateException("email already registered");
        });
    }
}
