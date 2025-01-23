package org.example.models;

import org.example.enums.UserRole;

public class UserFactory {

    // Factory method for creating users based on role
    public static User createUser(int id, String username, String password, UserRole role, String address, String phone) {
        switch (role) {
            case ADMIN:
                return createAdmin(id, username, password);
            case CUSTOMER:
                return createCustomer(id, username, password, address, phone);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    // Factory method to create an Admin
    private static Admin createAdmin(int id, String username, String password) {
        return new Admin(id, username, password);  // Admin does not need address or phone
    }

    // Factory method to create a Customer
    private static Customer createCustomer(int id, String username, String password, String address, String phone) {
        return new Customer(id, username, password, address, phone);
    }
}
