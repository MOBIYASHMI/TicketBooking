package com.example.application.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String busName;

    @Column(nullable = false)
    private String busType;

    private Integer totalSeats;

    @Column(nullable = false)
    private String registrationNumber;

    @OneToMany(mappedBy = "bus",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Schedule> schedules;

    public Bus() {
    }

    public Bus(Long id, String busName, String busType, Integer totalSeats, String registrationNumber, List<Schedule> schedules) {
        this.id = id;
        this.busName = busName;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.registrationNumber = registrationNumber;
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
}
