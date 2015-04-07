package com.github.matejonnet.demo;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.github.matejonnet.demo.Util.newArrayList;

/**
 * see http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html
 *     https://gist.github.com/Unisay/ff1aefdbd840c574395c
 *
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-12-19.
 */
public class CompletableFutureExample {

    Executor executor = Executors.newFixedThreadPool(4);

    @Test
    public void simpleCompletableFuture(Person person) {

    }

    @Test
    public void reactive(Person person) {
        fetchDetailsAsync(person)
            .thenCompose(pd -> sendEmail(pd))
            .handle((email, e) -> complete(email, e));
    }

    @Test
    public void reactiveWithStream() {
        List<Person> persons = newArrayList(
                new Person(1, "Bob", 25),
                new Person(2, "Alex", 20),
                new Person(3, "Cindy", 20),
                new Person(4, "Bill", 25));

        persons.parallelStream()
            .filter(p -> p.getAge() > 20)
            .map(p -> fetchDetailsAsync(p))
            .map(pdf -> pdf.thenCompose(pd -> sendEmail(pd)))
            .map(emailF -> emailF.handle((email, e) -> complete(email, e)));
    }

    private CompletableFuture<PersonWithDetails> fetchDetailsAsync(Person person) {
        return CompletableFuture.supplyAsync(() -> {
            return new PersonWithDetails(person);
        }, executor);
    }

    private CompletableFuture<Email> sendEmail(PersonWithDetails personWithDetails) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new Email(personWithDetails);
            } catch (Exception e) {
                throw new ExceptionWrapper(e);
            }
        }, executor);
    }

    private CompletableFuture<Boolean> complete(Email email, Throwable e) {
        return CompletableFuture.supplyAsync(() -> {
            if (e != null) {
                e.printStackTrace();
                return false;
            } else {
                System.out.println("Processed " + email);
                return true;
            }
        }, executor);
    }

}
