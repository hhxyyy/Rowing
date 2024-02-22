package nl.tudelft.sem.template.scheduler.validators;

import nl.tudelft.sem.template.scheduler.models.EventModel;
import nl.tudelft.sem.template.scheduler.models.UserRequest;

import java.util.Date;
import org.h2.engine.User;

public interface Validator {
    void setNext(Validator handler);

    int getPosition(UserRequest user);

    boolean handle(UserRequest user, EventModel event);
}
