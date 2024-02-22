package nl.tudelft.sem.template.scheduler.services;

import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.repos.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final transient NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getNotifications(String userId) {
        Optional<List<Notification>> outgoingRequests = notificationRepository.findNotificationByUserId(userId);
        Optional<List<Notification>> incomingRequests = notificationRepository.findNotificationByOwnerId(userId);

        List<Notification> notifications = new ArrayList<>();

        outgoingRequests.ifPresent(notifications::addAll);
        incomingRequests.ifPresent(notifications::addAll);

        return notifications;
    }
}
