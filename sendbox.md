@FunctionalInterface - target type
=====================

    // Assignment context
    Predicate<String> p = String::isEmpty;

    // Method invocation context
    stream.filter(e -> e.getSize() > 10)...

    // Cast context
    stream.map((ToIntFunction) e -> e.getSize())...


---

