package com.yourdomain;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users = new HashMap<>();

    public boolean addUser(String firstName, String lastName, String email, String password, String phone, String dietaryPreferences, String allergies) {
        if (users.containsKey(email)) {
            return false; // User already exists
        }
        users.put(email, new User(firstName, lastName, email, password, phone, dietaryPreferences, allergies));
        return true;
    }

    public User getUser(String email) {
        return users.get(email);
    }

    public boolean verifyUser(String email, String password) {
        User user = users.get(email);
        return user != null && user.getPasswordHash().equals(password);
    }
}
