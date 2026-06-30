package com.example.weatherviewer.service;

import com.example.weatherviewer.config.TestDatabaseConfig;
import com.example.weatherviewer.dto.SignUpDto;
import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
@Transactional
@Rollback
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void register_shouldPersistUserInDatabase() {
        SignUpDto signUpDto = new SignUpDto("testuser", "Password123", "Password123");

        User savedUser = userService.register(signUpDto);

        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getLogin());
    }

    @Test
    void register_withDuplicateLogin_shouldThrowException() {
        SignUpDto signUpDto = new SignUpDto("testuser", "Password123", "Password123");
        userService.register(signUpDto);

        SignUpDto duplicateDto = new SignUpDto("testuser", "Password456", "Password456");

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(duplicateDto));
    }

    @Test
    void register_shouldEncodePassword() {
        String rawPassword = "MySecret123!";
        SignUpDto dto = new SignUpDto("testuser", rawPassword, rawPassword);

        User savedUser = userService.register(dto);

        assertNotEquals(rawPassword, savedUser.getPassword());
        assertTrue(savedUser.getPassword().length() > rawPassword.length());
    }
}