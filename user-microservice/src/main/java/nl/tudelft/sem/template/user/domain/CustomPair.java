package nl.tudelft.sem.template.user.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


public class CustomPair<S, T> {
    private transient S first;
    private transient T second;

    public CustomPair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    public CustomPair() {
    }

    public S getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomPair<?, ?> that = (CustomPair<?, ?>) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public String toString() {
        return "CustomPair{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
