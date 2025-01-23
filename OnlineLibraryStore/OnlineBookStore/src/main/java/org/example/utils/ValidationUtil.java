package org.example.utils;

public class ValidationUtil {
    public static boolean isValidUsername(String username) {
        return username != null && username.length() <= 50 && username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidAddress(String address) {
        return address != null && address.length() <= 255;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*") &&
                password.matches(".*[!@#$%^&*].*");
    }
}
