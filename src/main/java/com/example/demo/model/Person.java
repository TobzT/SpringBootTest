package com.example.demo.model;

public class Person {
    private final String name;
    private final String dob;

    public Person(String name, String dob) {
        this.name = name;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }
}
