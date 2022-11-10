package com.example.demo.api;

import com.example.demo.model.Anniversary;
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

    private static HashMap<Integer, Integer> months = new HashMap<>();
    static {
        months.put(1, 31);
        months.put(2, 27);
        months.put(3, 31);
        months.put(4, 30);
        months.put(5, 31);
        months.put(6, 30);
        months.put(7, 31);
        months.put(8, 31);
        months.put(9, 30);
        months.put(10, 31);
        months.put(11, 30);
        months.put(12, 31);
    }

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
    public Map<String, Map<String, Integer>> getAnniversary() {
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
        int totalAge2 = 0;
        Map<String, Map<String, Integer>> output = new HashMap<>();
        Map<String, Integer> test = new HashMap<>();
        String newDate = "";
        int x = 0;
        // calculate new year
        while(totalAge2 != next50) {
            test.clear();
            output.clear();
            int dAge = next50 - totalAge2;
            if(dAge != next50) peopleLeft += dAge;
            if(peopleLeft < 0){
                while (peopleLeft < 0){
                    peopleLeft = persons.size() + peopleLeft;
                    x++;
                }

            }
            totalAge2 = 0;


            int currentYear = Integer.parseInt(time.substring(6, 10));
            int newYear = currentYear + fullYears + 1 - x;
            String date;
            if (peopleLeft > 0) {
                date = persons.get(peopleLeft - 1).getDobS();
            } else {
                date = persons.get(0).getDobS();
            }

            String newMonth = date.substring(3, 5);
            int newDay = Integer.parseInt(date.substring(0,2));
            newDay += 1;

            // update month and year if necessary
            if(newDay > months.get(Integer.parseInt(newMonth))) {
                newDay = 1;
                int a = Integer.parseInt(newMonth) + 1;
                if(a > 12) {
                    a = 1;
                    newYear++;
                }
                newMonth = Integer.toString(a);
            }


            newDate = format(Integer.toString(newDay)) + '/' + format(newMonth) + '/' + newYear;

//        test.put("next50", next50);


            for(Person person: persons) {
                int personAge = getAge(person.getDobS(), newDate);
                totalAge2 += personAge;
                test.put(person.getName(), personAge);
            }
        }


        output.put(newDate, test);

        return output;
    }

    private String format(String day) {
        if(day.length() == 1) {
            return "0" + day;
        }
        return day;
    }

    private int getAge(String dob, String time) {
        int age;
        int currentYear = Integer.parseInt(time.substring(6, 10));
        int currentMonth = Integer.parseInt(time.substring(3, 5));
        int currentDay = Integer.parseInt(time.substring(0, 2));

        int birthYear = Integer.parseInt(dob.substring(6, 10));
        int birthMonth = Integer.parseInt(dob.substring(3, 5));
        int birthDay = Integer.parseInt(dob.substring(0, 2));

        if (birthMonth > currentMonth) {
            age = currentYear - birthYear   - 1;
        } else if(birthMonth == currentMonth) {
            if(birthDay > currentDay) {
                age = currentYear - birthYear   - 1;
            } else {
                age = currentYear - birthYear;
            }
        } else {
            age = currentYear - birthYear;
        }
        return age;
    }

    private void sortPersons() {
        Collections.sort(persons, new Person.CustomComparator());
    }
}
