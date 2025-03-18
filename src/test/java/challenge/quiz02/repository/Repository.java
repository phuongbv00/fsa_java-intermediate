package challenge.quiz02.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<ID, E> {
    Optional<E> findById(ID id);

    Collection<E> findAll();

    Collection<E> findAll(Predicate<E> filter, Comparator<E> comparator);

    E save(E e);

    E update(E e);

    void delete(ID id);

    int countBy(Predicate<E> filter);
}
