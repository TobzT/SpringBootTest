package com.example.demo.model;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Person {
    private final String name;
    private final String dobS;
    private final Date dob;


    public Person(String name, String dob) {

        this.name = name;
        this.dobS = dob;
        try {
            this.dob = new SimpleDateFormat("dd/MM/yyyy").parse(dobS);
        } catch (Exception e) {
            throw new RuntimeException("NOT DATE COMPATIBLE");
        }
    }

    public String getName() {
        return name;
    }

    public Date getDob() {
        return dob;
    }

    public String getDobS() {
        return dobS;
    }


    public static class CustomComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o1.getDob().compareTo(o2.getDob());
        }
    }
}
