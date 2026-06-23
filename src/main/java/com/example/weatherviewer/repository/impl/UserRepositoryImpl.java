package com.example.weatherviewer.repository.impl;

import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String FIND_BY_LOGIN_HQL = "FROM User WHERE login = :login";
    private static final String LOGIN_PARAM = "login";

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_BY_LOGIN_HQL, User.class)
                .setParameter(LOGIN_PARAM, login)
                .uniqueResultOptional();
    }

    @Override
    public User save(User user) {
        sessionFactory.getCurrentSession().persist(user);
        return user;
    }
}