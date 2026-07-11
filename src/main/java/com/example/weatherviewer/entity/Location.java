package com.example.weatherviewer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "latitude", nullable = false, precision = 8, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    public Location(String name, User user, BigDecimal latitude, BigDecimal longitude, String country, String state) {
        this.name = name;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.state = state;
    }
}