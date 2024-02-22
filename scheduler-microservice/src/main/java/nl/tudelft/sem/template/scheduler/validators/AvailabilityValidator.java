package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.CustomPair;
import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;

import java.util.Date;

public class AvailabilityValidator extends BaseValidator {


    boolean checkValidRequest(UserRequest user, EventModel event) {
        return user.getAvailability() == null || user.getAvailability().isEmpty()
            || event.getTime() == null || checkIfNotEnoughBefore(event);
    }

    boolean checkIfNotEnoughBefore(EventModel event) {
        Date current = new Date();
        if (event.getIsCompetition() == EventModel.Type.COMPETITION) {
            return event.getTime().getFirst().getTime() - current.getTime() <= 1440 * 60 * 1000;
        }
        return event.getTime().getFirst().getTime() - current.getTime() <= 30 * 60 * 1000;
    }

    /**
     * Checks whether the event is compatible with any of the user's available dates.
     *
     * @param user  The user which made the matching request.
     * @param event The event that needs to be checked.
     * @return whether the event matches with the user.
     */
    @Override
    public boolean handle(UserRequest user, EventModel event) {
        if (checkValidRequest(user, event)) {
            return false;
        }
        for (CustomPair<Date, Date> moment : user.getAvailability()) {
            if (checkIfAvailable(moment, event.getTime())) {
                return superWrapper(user, event);
            }
            /*
            if ((moment.getFirst().equals(eventTime.getFirst())
                || moment.getFirst().before(eventTime.getFirst()))
                && (moment.getSecond().equals(eventTime.getSecond())
                || moment.getSecond().after(eventTime.getSecond()))) {
                return superWrapper(user, event);
            }*/
        }
        return false;
    }

    boolean checkIfAvailable(CustomPair<Date, Date> moment, CustomPair<Date, Date> eventTime) {
        return (moment.getFirst().equals(eventTime.getFirst())
            || moment.getFirst().before(eventTime.getFirst()))
            && (moment.getSecond().equals(eventTime.getSecond())
            || moment.getSecond().after(eventTime.getSecond()));
    }

    public boolean superWrapper(UserRequest user, EventModel event) {
        return super.checkNext(user, event);
    }
}
