package com.github.matejonnet.demo;

import org.junit.Test;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.matejonnet.demo.Util.newArrayList;
import static java.util.Comparator.comparing;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2015-03-22.
 */
public class LambdaExamples {

    @Test
    public void runnableExample() {

        Runnable runnable = new Runnable(){
            @Override
            public void run(){
                System.out.println("Hello world one!");
            }
        };

        runnable.run();
    }

    @Test
    public void runnableExampleJ8() {

        Runnable runnable = () -> System.out.println("Hello world two!");

        runnable.run();
    }

    @Test
    public void comparatorExample() {
        List<String> list = newArrayList("b", "a", "c", "b");
        System.out.println(list);

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        System.out.println(list);
    }

    @Test
    public void comparatorExampleJ8() {
        List<String> list = newArrayList("b", "a", "c", "b");
        System.out.println(list);

        list.sort((String o1, String o2) -> o1.compareTo(o2));

        System.out.println(list);
    }

    /**
     * Types are resolved at compile type
     */
    @Test
    public void comparatorExampleJ8NoType() {
        List<String> list = newArrayList("b", "a", "c", "b");
        System.out.println(list);

        list.sort((o1, o2) -> o1.compareTo(o2));

        System.out.println(list);
    }

    /**
     * Types are resolved at compile type
     */
    @Test
    public void comparatorExampleJ8HighOrderFunction() {
        List<String> list = newArrayList("Bob", "Alex", "Cindy", "Bill");
        List<Person> persons = list.stream().map(n -> new Person(n)).collect(Collectors.toList());
        System.out.println(persons);

        persons.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
//        persons.sort(Comparator.comparing(p -> p.getName())); //High order function takes a function and creates new function
        persons.sort(comparing(p -> p.getName()));
        persons.sort(comparing(Person::getName));

        System.out.println(persons);
    }

    /**
     * Types are resolved at compile type
     */
    @Test
    public void multiLevelComparing() {
        List<Person> persons = newArrayList(
                new Person("Bob", 25),
                new Person("Alex", 20),
                new Person("Cindy", 20),
                new Person("Bill", 25));

        System.out.println(persons);

        persons.sort(comparing(Person::getAge)
                .thenComparing(Person::getName));

        System.out.println(persons);
    }

    @Test(expected = ConcurrentModificationException.class)
    public void removingFromList() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        for (Person person : persons) {
            if (person.getName().startsWith("B")) {
                persons.remove(person);
            }
        }
    }

    @Test
    public void removingFromListJ8() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        persons.removeIf(p -> p.getName().startsWith("B"));

        System.out.println(persons);
    }

}
