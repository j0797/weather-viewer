package com.example.weatherviewer.repository;

import com.example.weatherviewer.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByLogin(String login);

    Optional<User> findById(Long id);

    User save(User user);
}