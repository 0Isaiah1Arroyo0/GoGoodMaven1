package com.yourdomain;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String dietaryPreferences;
    private String allergies;

    public User(String firstName, String lastName, String email, String passwordHash, String phoneNumber, String dietaryPreferences, String allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.dietaryPreferences = dietaryPreferences;
        this.allergies = allergies;
    }

    // Add getters for each field

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    public String getAllergies() {
        return allergies;
    }
}

