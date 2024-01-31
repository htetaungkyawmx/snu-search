package service;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final Map<String, User> authenticatedUsers = new HashMap<>();

    // Method to authenticate a user and store it in the service
    public static void authenticateUser(String userId, User user) {
        authenticatedUsers.put(userId, user);
    }

    // Method to check if a user is authenticated
    public static boolean isUserAuthenticated(String userId) {
        return authenticatedUsers.containsKey(userId);
    }

    // Method to retrieve an authenticated user
    public static User getAuthenticatedUser(String userId) {
        return authenticatedUsers.get(userId);
    }

    // Method to de-authenticate a user (log them out)
    public static void deauthenticateUser(String userId) {
        authenticatedUsers.remove(userId);
    }
}

