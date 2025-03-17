package lecture.fp;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FPTest {
    @Test
    public void higherOrderFunction() {
        // Function returns function
        Function<Integer, Function<Integer, Integer>> plusFunctionBuilder = x -> y -> y + x;
        Function<Integer, Function<Integer, Integer>> multiplyFunctionBuilder = x -> y -> y * x;

        Function<Integer, Integer> incrementFunction = plusFunctionBuilder.apply(1);
        Function<Integer, Integer> doubleValueFunction = multiplyFunctionBuilder.apply(2);

        System.out.println(incrementFunction.apply(3));
        System.out.println(doubleValueFunction.apply(3));

        // Function as argument
        Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(t -> t % 2 == 0)
                .map(incrementFunction)
                .forEach(i -> System.out.print(i + " "));
    }

    @Test
    public void functionalComposition() {
        Function<Integer, Integer> incrementFunction = x -> x + 1;
        Function<Integer, Integer> doubleValueFunction = x -> x * 2;

        Function<Integer, Integer> incrementThenDouble = incrementFunction.andThen(doubleValueFunction);
        Function<Integer, Integer> doubleThenIncrement = incrementFunction.compose(doubleValueFunction);

        System.out.println(incrementThenDouble.apply(3));
        System.out.println(doubleThenIncrement.apply(3));
    }

    @Test
    public void streamLazyExecution() {
        List<String> list = List.of("abc1", "abc2", "abc3");
        Optional<String> searched = list.stream()
                .filter(element -> {
                    System.out.println("filter() was called");
                    return element.contains("2");
                })
                .map(element -> {
                    System.out.println("map() was called");
                    return element.toUpperCase();
                })
                .findFirst();
    }

    @Test
    public void parallelStream() {
        List.of(1, 2, 3, 4, 5, 6, 7).parallelStream()
                .forEach(n -> System.out.println(Thread.currentThread().getName() + " - " + n));
    }

    @Test
    public void collect() {
        System.out.println(Stream.of(1, 2, 3, 4, 5, 6, 7).collect(Collectors.toList()).getClass().getCanonicalName());
        System.out.println(Stream.of(1, 2, 3, 4, 5, 6, 7).toList().getClass().getCanonicalName());

        List<Person> people = List.of(
                new Person("Alice", 24),
                new Person("Bob", 37),
                new Person("Cong", 15),
                new Person("Duc", 81),
                new Person("Ellie", 7),
                new Person("Phuong", 15),
                new Person("Gojo", 15),
                new Person("Hanh", 48),
                new Person("Xiao Mao", 24)
        );
        System.out.println(people.stream().collect(Collectors.toMap(Person::getName, Person::getAge)));
        System.out.println(people.stream().map(Person::getName).collect(Collectors.joining(", ", "[", "]")));
        System.out.println(people.stream().map(Person::getAge).collect(Collectors.averagingInt(i -> i)));
        System.out.println(people.stream().collect(Collectors.groupingBy(Person::getAge)));
        System.out.println(people.stream().collect(Collectors.groupingBy(Person::getAge, Collectors.counting())));
        System.out.println(people.stream().collect(Collectors.partitioningBy(p -> p.getAge() > 10)));
        Map<Boolean, Long> peopleByAgePartitions = people.stream().collect(Collectors.partitioningBy(p -> p.getAge() > 10, Collectors.counting()));
        System.out.println(peopleByAgePartitions);
        System.out.println(peopleByAgePartitions.getClass().getCanonicalName());
        Map<Boolean, Long> unmodifiedPeopleByAgePartitions = people.stream()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.partitioningBy(p -> p.getAge() > 10, Collectors.counting()),
                                Collections::unmodifiableMap)
                );
        System.out.println(unmodifiedPeopleByAgePartitions);
        System.out.println(unmodifiedPeopleByAgePartitions.getClass().getCanonicalName());
    }

    @Test
    public void reduce() {
        int reducedTwoParams =
                IntStream.range(1, 4).reduce(10,
                        (a, b) -> {
                            System.out.println("accumulator was called: acc=" + a + ", cur=" + b);
                            return a + b;
                        });
        System.out.println(reducedTwoParams);
        System.out.println("===");
        int reducedParallel = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9).parallelStream()
                .reduce(10,
                        (a, b) -> {
                            System.out.println("accumulator was called: acc=" + a + ", cur=" + b);
                            return a + b;
                        },
                        (a, b) -> {
                            System.out.println("combiner was called: left=" + a + ", right=" + b);
                            return a + b;
                        });
        System.out.println(reducedParallel);
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
