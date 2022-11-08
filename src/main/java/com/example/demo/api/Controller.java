package com.example.demo.api;

import com.example.demo.model.Person;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    List<Person> persons = new ArrayList<Person>();

    @PostMapping("persons")
    public void createPerson(@RequestParam(value="name", defaultValue="user") String name, @RequestParam(value="dob", defaultValue="1/1/2000") String dob) {
        Person user = new Person(name, dob);
        persons.add(user);

    }

    @GetMapping("persons")
    public List<Person> readPersons(){
        return persons;
    }

    @DeleteMapping("persons")
    public void removeAllPersons() {
        persons.clear();
    }

}
