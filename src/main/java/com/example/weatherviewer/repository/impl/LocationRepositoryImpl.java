package com.example.weatherviewer.repository.impl;

import com.example.weatherviewer.entity.Location;
import com.example.weatherviewer.repository.LocationRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private static final String FIND_BY_USER_ID_AND_NAME_HQL =
            "FROM Location WHERE user.id = :userId AND lower(name) = lower(:name)";
    private static final String NAME_PARAM = "name";
    private static final String FIND_ALL_BY_USER_ID_HQL = "FROM Location WHERE user.id = :userId";
    private static final String USER_ID_PARAM = "userId";

    private final SessionFactory sessionFactory;

    public LocationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Location> findAllByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_ALL_BY_USER_ID_HQL, Location.class)
                .setParameter(USER_ID_PARAM, userId)
                .list();
    }

    @Override
    public Optional<Location> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Location.class, id));
    }

    @Override
    public Optional<Location> findByUserIdAndName(Long userId, String name) {
        return sessionFactory.getCurrentSession()
                .createQuery(FIND_BY_USER_ID_AND_NAME_HQL, Location.class)
                .setParameter(USER_ID_PARAM, userId)
                .setParameter(NAME_PARAM, name)
                .uniqueResultOptional();
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