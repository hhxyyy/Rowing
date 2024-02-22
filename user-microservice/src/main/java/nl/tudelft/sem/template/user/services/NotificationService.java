package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.authentication.AuthManager;

import java.io.IOException;

public interface NotificationService {
    String getNotifications(AuthManager username) throws IOException, InterruptedException;
}
