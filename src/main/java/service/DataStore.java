package service;

import model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataStore {
    private static final Map<String, User> userDatabase = new HashMap<>();

    public static void addUser(User user) {
        userDatabase.put(user.getId(), user);
    }

    public static User getUser(String id) {
        return userDatabase.get(id);
    }

    public static List<User> getAllUsers() {
        return userDatabase.values()
                .stream()
                .collect(Collectors.toList());
    }
}
