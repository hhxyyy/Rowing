package nl.tudelft.sem.template.event.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomPair<S, T> {
    private S first;
    private T second;

    public CustomPair(S first, T second) {
        this.first = first;
        this.second = second;
    }
}
