package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;

import java.util.Date;

public class CompetitionValidator extends BaseValidator {
    @Override
    public boolean handle(UserRequest user, EventModel event) {
        if (event.getIsCompetition() == EventModel.Type.COMPETITION) {
            for (String requirement : event.getRequirements().keySet()) {
                if (requirement.equals("Certificate")) {
                    continue;
                }
                if (requirement.equals("Professional")
                        && Boolean.parseBoolean(event.getRequirements().get(requirement))
                        && !Boolean.parseBoolean(user.getRequirements().get(requirement))) {
                    return false;
                } else if (!user.getRequirements().containsKey(requirement)
                    || !user.getRequirements().get(requirement).equals(event.getRequirements().get(requirement))) {
                    return false;
                }
            }
        }
        return superWrapper(user, event);
    }

    public boolean superWrapper(UserRequest user, EventModel event) {
        return super.checkNext(user, event);
    }
}
