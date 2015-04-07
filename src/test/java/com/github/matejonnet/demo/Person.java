package com.github.matejonnet.demo;

/**
* Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2015-04-06.
*/
public class Person {
    private int id;
    private String name;
    private int age;

    Person(String name) {
        this.name = name;
        age = -1;
    }

    Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    Person(int id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        String ret = name;
        if (age > 0) {
            ret += "(" + age + ")";
        }
        return ret;
    }

    public int getId() {
        return id;
    }
}
