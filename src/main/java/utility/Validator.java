package utility;

import model.User;
import service.DataStore;

public class Validator {
    public static boolean isValidToRegister(String id) {
        // Check if the ID is not empty and does not exist in a case-insensitive manner
        User user = DataStore.getUser(id.toLowerCase());
        return id != null && user == null;
    }

    public static boolean isValidPassword(String password) {
        if (password.length() < 4 || !Character.isLetter(password.charAt(0))) {
            return false; // Password is too short or doesn't start with an alphabet character
        }

        // Check if the password contains only allowed characters (@ and %)
        for (char c : password.toCharArray()) {
            if (!Character.isLetter(c) && c != '@' && c != '%') {
                return false; // Password contains disallowed characters
            }
        }

        return true; // Password meets all requirements
    }
}
