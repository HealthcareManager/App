package com.luce.healthmanager;

import java.time.LocalDate;

public class UserResponse {
    private String id;
    private String password;
    private String username;
    private String email;
    private Double height;
    private Double weight;
    private String gender;
    private LocalDate dateOfBirth;
    private String imagelink;
    private String jwtToken;
    private String role;

    public UserResponse(){}

    public  UserResponse(String email) {
        this.email = email;
    }

    public UserResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserResponse(String id, String password, String username, String email, Double height, Double weight, String gender, LocalDate dateOfBirth, String imagelink, String jwtToken, String role) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.imagelink = imagelink;
        this.jwtToken = jwtToken;
        this.role = role;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
