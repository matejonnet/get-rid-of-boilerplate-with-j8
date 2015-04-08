package com.github.matejonnet.demo;

import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.matejonnet.demo.Util.newArrayList;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2015-04-05.
 */
public class StreamExamples {

    @Test
    public void forEachLoop() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        for (Person person : persons) {
            System.out.println(person.getName());
        }
    }

    @Test
    public void forEachLoopJ8() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        persons.forEach(p -> System.out.println(p.getName()));

        persons.forEach(System.out::println); //=System.out.println(p)
    }

    @Test
    public void basicStream() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        List<String> personNamesOver20 = persons.stream()
                .filter(p -> p.getAge() > 20)
                .map(Person::getName)
                .collect(Collectors.toList());

        System.out.println(personNamesOver20);
    }

    Predicate<Person> filterAge(Integer age) {
        return (p) -> {
            return p.getAge() > age;
        };
    }

    @Test
    public void filter() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        Predicate<Person> filterAge20 = (p) -> {
            return p.getAge() > 20;
        };

        List<Person> personsOver20 = persons.stream()
//                .filter(p -> p.getAge() > 20)
//                .filter(filterAge20)
                .filter(filterAge(20))
                .collect(Collectors.toList());

        System.out.println(personsOver20);
    }


    @Test
    public void concatenations() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        String personsString = persons.stream()
                .filter(p -> p.getAge() > 20)
                .sorted(Comparator.comparing(Person::getName))
                .map(Person::getName)
                .collect(Collectors.joining(", "));

        System.out.println(personsString);
    }

    @Test
    public void concatenationsWithFormat() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        Function<Person, String> formatString  = (p) -> {
            return p.getName() + " is " + p.getAge() + " years old.";
        };

        //Function<Person, String> formatString  = (p) -> p.getName() + " is " + p.getAge() + " years old.";

        String personsString = persons.stream()
                .filter(p -> p.getAge() > 20)
                .sorted(Comparator.comparing(Person::getName))
                .map(p -> p.getName() + " is " + p.getAge() + " years old.")
              //.map(formatString)
                .collect(Collectors.joining("\n"));

        System.out.println(personsString);
    }

    @Test
    public void singletonCollector() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        Person person = persons.stream()
                .filter(p -> p.getId() == 1)
                .collect(StreamCollectors.singletonCollector());

        Person person2 = persons.stream()
                .filter(p -> p.getId() == 2)
                .findFirst()
                .orElse(null);

        System.out.println(person);
        System.out.println(person2);
    }

    @Test
    public void mapReduce() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        int totalAge = persons.stream()
                .map(Person::getAge)
                .reduce(0, (left, right) -> left + right);

        System.out.println(totalAge);

        int totalAgeWithSum = persons.stream()
                .mapToInt(Person::getAge)
                .sum();

        System.out.println(totalAgeWithSum);
    }

}
