package com.example.demo.api;

import com.example.demo.model.Person;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class Controller {

    List<Person> persons = new ArrayList<Person>();

    DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

    @PostMapping("persons")
    public void createPerson(@RequestParam(value="name", defaultValue="user") String name, @RequestParam(value="dob", defaultValue="01/01/2000") String dob) {

        Person user = new Person(name, dob);
        persons.add(user);

    }

    @GetMapping("persons")
    public List<Person> readPersons(){
        return persons;
    }

    @GetMapping("persons/{id}")
    public Person readPerson(@PathVariable int id) throws Exception {
        Person output = null;
        try{
             output =  persons.get(id - 1);


        } catch (Exception e) {
            throw new RuntimeException("ID out of range");
        }
        return output;
    }

    @DeleteMapping("persons")
    public void removeAllPersons() {
        persons.clear();
    }

    @DeleteMapping("persons/{id}")
    public void removePerson(@PathVariable int id) throws Exception {
        try {
            Person removed = persons.remove(id - 1);
        } catch (Exception e) {
            throw new RuntimeException("ID out of range");
        }
    }

    @GetMapping("persons/anniversary")
    public String getAnniversary() {
        sortPersons();
        int totalAge = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        String time = dtf.format(now);

        for(Person person : persons) {
            String dob = person.getDobS();
            totalAge += getAge(dob, time);
        }

        // get next time an anniversary happens (50, 100, 150 etc.)
        float we = (float)totalAge / 50;
        int next50 = (int)((Math.ceil(we) * 50));

        // get delta time
        int dTime = next50 - totalAge;

        // get amount of years ahead
        float years = (float)dTime / persons.size();

        // get amount of full years ahead
        int fullYears = (int) Math.floor(years);

        // get the rest
        float rest = years - fullYears;

        // get how many people need to have their birthday into the year
        int peopleLeft = (int) Math.ceil((rest * persons.size()));

        // calculate new year
        int currentYear = Integer.parseInt(time.substring(6, 10));
        int newYear = currentYear + fullYears;

        String date = persons.get(peopleLeft - 1).getDobS();
        String newMonth = date.substring(3, 5);
        int newDay = Integer.parseInt(date.substring(0,2));
        // TODO VALIDATION OF NEW MONTH

        newDay += 1;

        String newDate = format(Integer.toString(newDay)) + '/' + newMonth + '/' + newYear;


        return newDate;
    }

    private String format(String day) {
        if(day.length() == 1) {
            return "0" + day;
        }
        return day;
    }

    private int getAge(String dob, String time) {
        int dYear;
        int currentYear = Integer.parseInt(time.substring(6, 10));
        int currentMonth = Integer.parseInt(time.substring(3, 5));
        int currentDay = Integer.parseInt(time.substring(0, 2));

        int birthYear = Integer.parseInt(dob.substring(6, 10));
        int birthMonth = Integer.parseInt(dob.substring(3, 5));
        int birthDay = Integer.parseInt(dob.substring(0, 2));

        if (birthMonth > currentMonth) {
            dYear = currentYear - birthYear - 1;
        } else if(birthMonth == currentMonth) {
            if(birthDay > currentDay) {
                dYear = currentYear - birthYear - 1;
            } else {
                dYear = currentYear - birthYear;
            }
        } else {
            dYear = currentYear - birthYear;
        }
        return dYear;
    }

    private void sortPersons() {
        Collections.sort(persons, new Person.CustomComparator());
    }
}
