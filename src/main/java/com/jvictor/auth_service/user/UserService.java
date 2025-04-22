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
    private final ConfirmationTokenService confirmationTokenService;

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

    @Transactional
    public String signUpUser(User user) {
        Optional<User> userOpt = userRepository.findByEmail(user.getEmail());

        if (userOpt.isPresent()) {
            throw new IllegalStateException("email already registered.");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        this.saveUser(user);

        return confirmationTokenService.generateConfirmationTokenForUser(user);
    }

}
