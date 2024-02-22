package nl.tudelft.sem.template.scheduler.services;

import nl.tudelft.sem.template.scheduler.domains.Notification;
import nl.tudelft.sem.template.scheduler.repos.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class NotificationServiceTest {

    @Autowired
    private transient MockMvc mockMvc;

    NotificationServiceImpl service;

    @MockBean
    NotificationRepository notificationRepository;

    @BeforeEach
    void setup() {
        service = new NotificationServiceImpl(notificationRepository);
    }

    @Test
    void getNotificationsNone() {
        when(notificationRepository.findNotificationByUserId(any())).thenReturn(Optional.empty());
        when(notificationRepository.findNotificationByOwnerId(any())).thenReturn(Optional.empty());

        assertThat(service.getNotifications("user")).isEmpty();
    }

    @Test
    void getNotificationsOutgoing() {
        List<Notification> notifications = List.of(new Notification("a", "b", 1, 2),
                new Notification("c", "d", 3, 4));

        when(notificationRepository.findNotificationByUserId(any())).thenReturn(Optional.of(notifications));
        when(notificationRepository.findNotificationByOwnerId(any())).thenReturn(Optional.empty());

        assertThat(service.getNotifications("user")).containsAll(notifications);
    }

    @Test
    void getNotificationsIncoming() {
        List<Notification> notifications = List.of(new Notification("a", "b", 1, 2),
                new Notification("c", "d", 3, 4));

        when(notificationRepository.findNotificationByUserId(any())).thenReturn(Optional.empty());
        when(notificationRepository.findNotificationByOwnerId(any())).thenReturn(Optional.of(notifications));

        assertThat(service.getNotifications("user")).containsAll(notifications);
    }

    @Test
    void getNotificationsBoth() {
        List<Notification> notifications1 = List.of(new Notification("a", "b", 1, 2),
                new Notification("c", "d", 3, 4));
        List<Notification> notifications2 = List.of(new Notification("e", "f", 1, 2),
                new Notification("g", "h", 3, 4));

        when(notificationRepository.findNotificationByUserId(any())).thenReturn(Optional.of(notifications1));
        when(notificationRepository.findNotificationByOwnerId(any())).thenReturn(Optional.of(notifications2));

        List<Notification> expected = new ArrayList<>();
        expected.addAll(notifications1);
        expected.addAll(notifications2);

        assertThat(service.getNotifications("user")).containsAll(expected);
    }
}