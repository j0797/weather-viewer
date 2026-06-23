package com.example.weatherviewer.repository.impl;

import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.repository.LocationRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private static final String FIND_BY_USER_ID_HQL = "FROM Location WHERE user.id = :userId";
    private static final String USER_ID_PARAM = "userId";

    private final SessionFactory sessionFactory;

    public LocationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Location> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_BY_USER_ID_HQL, Location.class)
                .setParameter(USER_ID_PARAM, userId)
                .list();
    }

    @Override
    public void deleteById(Long id) {
        Location location = sessionFactory.getCurrentSession().get(Location.class, id);
        if (location != null) {
            sessionFactory.getCurrentSession().remove(location);
        }
    }

    @Override
    public Location save(Location location) {
        sessionFactory.getCurrentSession().persist(location);
        return location;
    }
}