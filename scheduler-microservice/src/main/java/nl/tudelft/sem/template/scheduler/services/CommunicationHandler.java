package nl.tudelft.sem.template.scheduler.services;

import java.io.IOException;

public interface CommunicationHandler {

    String getEventOwner(long eventId) throws IOException, InterruptedException;


}
