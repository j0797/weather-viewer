package com.example.weatherviewer.service;

import com.example.weatherviewer.dto.auth.SignUpDto;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.auth.UserAlreadyExistsException;
import com.example.weatherviewer.exception.auth.UserNotFoundException;
import com.example.weatherviewer.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import at.favre.lib.crypto.bcrypt.BCrypt;

@Service
public class UserService {

    private static final int BCRYPT_COST = 12;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(SignUpDto signUpDto) {
        String login = signUpDto.login();
        String rawPassword = signUpDto.password();

        if (!signUpDto.password().equals(signUpDto.passwordConfirmation())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.findByLogin(login).isPresent()) {
            throw new UserAlreadyExistsException("User with login " + login + " already exists");
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, rawPassword.toCharArray());
        User user = new User(login, hashedPassword);
        try {
            return userRepository.save(user);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException("User with login " + login + " already exists");
        }
    }

    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}