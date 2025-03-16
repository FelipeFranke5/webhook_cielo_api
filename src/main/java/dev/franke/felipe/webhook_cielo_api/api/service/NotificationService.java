package dev.franke.felipe.webhook_cielo_api.api.service;

import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.repository.NotificationRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification getNotificationByPaymentId(String paymentId) {
        try {
            UUID.fromString(paymentId);
        } catch (IllegalArgumentException e) {
            throw new InvalidNotificationIdException("Invalid paymentId");
        }
        Notification notification = notificationRepository.findByPaymentId(paymentId);
        if (notification == null) {
            throw new NotificationNotFoundException("Notification not found");
        }
        return notification;
    }

}
