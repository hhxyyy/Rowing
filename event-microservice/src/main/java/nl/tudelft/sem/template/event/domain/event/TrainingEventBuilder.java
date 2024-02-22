package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;

import java.util.HashMap;
import java.util.Map;

public class TrainingEventBuilder extends EventBuilder {
    public TrainingEventBuilder() {
        this.type = Event.Type.TRAINING;
    }

    @Override
    public void addRequirements(Map<String, String> requirements) throws InvalidModelException {
        if (requirements == null) {
            this.requirements = new HashMap<>();
            return;
        }

        if (requirements.entrySet().stream().anyMatch(entry -> entry.getKey().isBlank() || entry.getValue().isBlank())) {
            throw new InvalidModelException("Requirements contains blank field");
        }

        super.requirements = requirements;
    }
}
