package org.example.models;

public class Category {
    private static int idCounter = 1;
    private int id;
    private String name;

    // Constructor
    public Category(String name) {
        this.id = idCounter++;
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
