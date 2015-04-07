package com.github.matejonnet.demo;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    public void simpleCompletableFuture() throws ExecutionException, InterruptedException {

        final CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> longRunningTask(future)).start();

        System.out.println("Long running task started         @" + System.currentTimeMillis());

        System.out.println("Waiting for get ...               @" + System.currentTimeMillis());

        future.get();
        System.out.println("Get: Long running task completed  @" + System.currentTimeMillis());
    }

    private void longRunningTask(CompletableFuture<String> future) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            future.completeExceptionally(e);
        }
        future.complete("42");
    }

    @Test
    public void completableFutureWithSupplier() throws ExecutionException, InterruptedException {

        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                System.out.println("Long running task started @" + System.currentTimeMillis());
                sleepQuietly();
                return "42";
            }, executor);

        future.thenApply(Integer::parseInt)
              .thenApply(r -> r * r * Math.PI)
              .thenAccept((p) -> System.out.println("Result :" + p.toString()))
              .thenRun(() -> System.out.println("Completed                 @" + System.currentTimeMillis()));

        System.out.println("Waiting for get ...       @" + System.currentTimeMillis());

        sleepQuietly(1500);
    }

    @Test
    public void combining() {
        Person person = new Person("Bob", 25);

        fetchDetailsAsync(person)
            .thenCompose(pd -> sendEmail(pd))
            .handle((email, e) -> complete(email, e));
    }

    @Test
    public void combiningOnStream() {
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

    private void sleepQuietly() {
        sleepQuietly(1000);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ExceptionWrapper(e);
        }
    }

}
