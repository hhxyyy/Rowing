package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.models.EventModel;
import nl.tudelft.sem.template.user.models.UserApprovalModel;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface OwnerService {
    HttpResponse<String> createEvent(EventModel event) throws IOException, InterruptedException;

    HttpResponse<String> editEvent(long eventId, EventModel event) throws IOException, InterruptedException;

    HttpResponse<String> cancelEvent(long eventId) throws IOException, InterruptedException;

    String approveApplicant(UserApprovalModel body) throws IOException, InterruptedException;
}
