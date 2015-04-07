
Get rid of boilerplate code with Java 8
=======================================
<br />

    persons.stream()
        .filter(p -> p.getAge() > 20)
        .map(p -> fetchDetailsAsync(p))
        .map(pdf -> pdf.thenCompose(pd -> sendEmail(pd)))
        .map(emailF -> emailF.handle((email, e) -> complete(email, e)));

<br />
Matej Lazar [matejonnet@gmail.com](matejonnet@gmail.com)

???

Visit [http://matejonnet.github.io/get-rid-of-boilerplate-with-j8/] (http://matejonnet.github.io/get-rid-of-boilerplate-with-j8/) to see slides in presentation mode. 

---

Agenda
======

### - Lambda expressions

### - Streams

### - CompletableFuture

---

Lambda expressions
==================

- Lambda is a function, function takes parameters and returns result

- Pre Java 8 functions could be implemented using methods

- *A Lambda enables functions to be passed around*

- Default method: method in interface (you don't break back compatibility)

- Method references "::" are used for lambda shortcuts

- Higher order function takes a function and creates new function

---

Anonymous inner vs Lambda expression
====================================

### Anonymous Inner Class

    Runnable r1 = new Runnable(){
        @Override
        public void run(){
            System.out.println("Hello world one!");
        }
    };
    
### Lambda expression
    
    Runnable r2 = () -> System.out.println("Hello world two!");

---

@FunctionalInterface
====================

- Is an interface where you have to implement single method (single abstract method)

- collections
    - Predicate<T>
    - Comparator<T>

- java.util.function
    - Supplier<T>
    - Consumer<T>
    - Function<T,R>

- https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html

---

Comparator with Lambda expression
=================================

### Anonymous Inner Class

    list.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    });

### Lambda expression

    list.sort((String o1, String o2) -> o1.compareTo(o2));
    list.sort((o1, o2) -> o1.compareTo(o2));

---

Comparator with Higher order function
=====================================

### Higher order function
    
    persons.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    
    //Higher order function
    persons.sort(Comparator.comparing(p -> p.getName()));
    
    //Method references
    persons.sort(Comparator.comparing(Person::getName)); 

### Using static import and multi level comparing
    
    persons.sort(comparing(Person::getName));
    
    persons.sort(comparing(Person::getAge).thenComparing(Person::getName));

---

New methods on List and Collection
==================================

Useful *default* methods added to list and collection interfaces 

Removing element from list and avoiding ConcurrentModificationException
- no classical for loop -> iterator.remove
- collection.removeIf


    for (Person person : persons) {
        if (person.getName().startsWith("B")) {
            persons.remove(person);
        }
    }
    
    persons.removeIf(p -> p.getName().startsWith("B"));

Similar: list.replaceAll


---


Streams
=======
- forEach
- stream
    - filter(predicate)
    - map
    - collect (collector / reducer)
        - java.util.stream.Collectors
        - SingletonCollector
        - string concatenating

---

ForEach
=======

    for (Person person : persons) {
        System.out.println(person.getName());
    }

    persons.forEach(p -> System.out.println(p.getName()));
    
    //System.out.println(p)
    persons.forEach(System.out::println);

---

Streams
=======

### Common stream usage
    List<String> personNamesOver20 = persons.stream()
            .filter(p -> p.getAge() > 20)
            .map(Person::getName)
            .collect(Collectors.toList());

---

Streams :: map / reduce
=======================

### Stream.reduce
    int totalAge = persons.stream()
            .map(Person::getAge)
            .reduce(0, (a, b) -> a + b);

### IntStream.sum
    int totalAgeWithSum = persons.stream()
            .mapToInt(Person::getAge)
            .sum();

---

Streams :: filter
=================

Remove elements form stream 

        List<Person> personsOver20 = persons.stream()
                //.filter(p -> p.getAge() > 20)
                //.filter(filterAge20)
                .filter(filterAge(20))
                .collect(Collectors.toList());

---

Streams :: String concatenating
===============================

    String personsString = persons.stream()
            .filter(p -> p.getAge() > 20)
            .sorted(Comparator.comparing(Person::getName))
            .map(Person::getName)
            .collect(Collectors.joining(", "));

With formatting

    String personsString = persons.stream()
            .filter(p -> p.getAge() > 20)
            .sorted(Comparator.comparing(Person::getName))
            .map(p -> p.getName() + " is " + p.getAge() + " years old.")
            //.map(formatString)
            .collect(Collectors.joining("\n"));

---

Streams :: SingletonCollector
=============================

Reduces result to single instance 

    Person person = persons.stream()
        .filter(p -> p.getId() == 1)
        .collect(StreamCollectors.singletonCollector());

---

CompletableFuture
=================
- Reactive programming
- Extending Future
- Exception handling

---

Reactive programming
====================

- Reactive programming is programming with asynchronous data streams
- A stream is a sequence of ongoing events ordered in time
- We capture these emitted events asynchronously, by defining a function that will execute when a value is emitted
- Observer Design Pattern

---

Extending java.util.concurrent.Future
=====================================

- Extends Future by adding async support
- .complete(T value)
- .completeExceptionally(Throwable ex)
- .get() -> inherited from Future *blocking*
- .then\* -> *async*
- .thenCompose(Function) -> function is called on complete
- .handle(BiFunction(T result, Throwable e))

---

CompletableFuture :: simple 
===========================

    CompletableFuture<String> future = new CompletableFuture<>();
    new Thread(() -> longRunningTask(future)).start();

    //blocks until future.complete is called
    future.get();

    private void longRunningTask(CompletableFuture<String> future) {
        //long running operations
        future.complete("42");
    }

Calling complete unblocks all clients waiting for get 
and also calls functions that are referenced by .then\* methods.     

---

CompletableFuture :: Supplier
=============================

    final CompletableFuture<String> future = 
        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                //...long running...
                return "42";
            }
        }, executor);
    
### With lambda 
    
    final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
        //...long running...
        return "42";
    }, executor);
    
---

CompletableFuture :: acting 
===========================

    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleepQuietly();
            return "42";
        }, executor);

    future.thenApply(Integer::parseInt)
          .thenApply(r -> r * r * Math.PI)
          .thenAccept(p -> System.out.println("Result :" + p.toString()));

Thread is not blocked when we use .then\* methods. 
---

CompletableFuture :: exception handling 
=======================================
- Only runtime exceptions can be thrown out of lambda
- All exceptions must be wrapped in RuntimeException 
- Main thread completed we cannot catch the exception
- The most flexible approach is using .handle() 
- handle() takes a function receiving either correct result or exception


    future.thenApply(Integer::parseInt)
          .thenApply(r -> r * r * Math.PI)
          .thenAccept((p) -> System.out.println("Result :" + p.toString()))
          .handle((result, ex) -> {
                if (result != null) storeResult(result);
                else handleException(ex);
          });

---

CompletableFuture :: combining 
==============================

Combining (chaining) two futures (thenCompose())

    public void combining(Person person) {
        fetchDetailsAsync(person)
            .thenCompose(pd -> sendEmail(pd))
            .handle((email, e) -> complete(email, e));
    }

    private CompletableFuture<PersonWithDetails> fetchDetailsAsync(Person person) {
        return CompletableFuture.supplyAsync(() -> {
            return new PersonWithDetails(person);
        }, executor);
    }

---


CompletableFuture :: with stream 
================================

    persons.parallelStream()
        .filter(p -> p.getAge() > 20)
        .map(p -> fetchDetailsAsync(p))
        .map(pdf -> pdf.thenCompose(pd -> sendEmail(pd)))
        .map(emailF -> emailF.handle((email, e) -> complete(email, e)));

---

Links
=====

### References

[Oracle Lambda-QuickStart](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html)<br />
[Java SE 8 - lntroduction to Lambda Expressions](http://javaswiki.blogspot.com/2015/03/java-se-8-lntroduction-to-lambda-expressions.html)<br />
http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html<br />
https://gist.github.com/Unisay/ff1aefdbd840c574395c<br />

### Presentation tool used
https://github.com/gnab/remark/

*Matej Lazar (matejonnet@gmail.com, mlazar@redhat.com)*
