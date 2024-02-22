package nl.tudelft.sem.template.event.domain.event;

import nl.tudelft.sem.template.event.domain.event.repo.Event;
import nl.tudelft.sem.template.event.exceptions.InvalidModelException;

import java.util.HashMap;
import java.util.Map;

public class CompetitionEventBuilder extends EventBuilder {
    public CompetitionEventBuilder() {
        this.type = Event.Type.COMPETITION;
    }

    @Override
    public void addRequirements(Map<String, String> requirements) throws InvalidModelException {
        if (requirements == null) {
            this.requirements = new HashMap<>();
            return;
        }

        if (requirements.entrySet().stream().anyMatch(entry -> entry.getKey().isBlank() || entry.getValue().isBlank())) {
            throw new InvalidModelException("Requirements contains blank field");
        } else if (!requirements.containsKey("Gender")) {
            throw new InvalidModelException("Requirements did not include the gender");
        } else if (!requirements.containsKey("Organization")) {
            throw new InvalidModelException("Requirements did not include the organization");
        }

        this.requirements = requirements;
    }
}
