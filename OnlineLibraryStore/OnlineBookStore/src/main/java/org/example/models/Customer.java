package org.example.models;

import org.example.enums.UserRole;

public class Customer extends User {
    private String address;
    private String phone;

    public Customer(int id, String username, String password, String address, String phone) {
        super(id, username, password, UserRole.CUSTOMER);
        this.address = address;
        this.phone = phone;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public void displayCapabilities() {
        System.out.println("Customer Capabilities: Browse Books, Manage Cart, Place Orders, and Write Reviews.");
    }

    @Override
    public String toFileString() {
        return getId() + "," + getUsername() + "," + getPassword() + ",CUSTOMER," + address + "," + phone;
    }

    @Override
    public String toString() {
        return super.toString() + ", address='" + address + '\'' + ", phone='" + phone + '\'' + '}';
    }
}
