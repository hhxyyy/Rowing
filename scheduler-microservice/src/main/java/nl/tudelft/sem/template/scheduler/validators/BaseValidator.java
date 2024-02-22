package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;

import java.util.Date;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public void setNext(Validator h) {
        this.next = h;
    }

    /**
     * Get the SINGLE position from the user Request.
     *
     * @param user the user request
     * @return a number from 0 to 4 representing the index of the chosen pos
     */
    public int getPosition(UserRequest user) {
        for (int i = 0; i < 5; i++) {
            if (user.getPositions()[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Send the validation process to the next validator.
     *
     * @param user the user request
     * @param event the current event that needs to be filtered
     * @return the result of the next validator
     */
    public boolean checkNext(UserRequest user, EventModel event) {
        if (next == null) {
            return true;
        }
        return next.handle(user, event);
    }
}
