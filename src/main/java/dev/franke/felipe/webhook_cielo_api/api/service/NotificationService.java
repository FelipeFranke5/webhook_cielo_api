package dev.franke.felipe.webhook_cielo_api.api.service;

import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification getNotificationByPaymentId(String paymentId) {
        String notFoundMessage = "Notification with Payment ID " + paymentId + " not found";
        try {
            UUID.fromString(paymentId);
        } catch (IllegalArgumentException illegalArgumentException) {
            String invalidPaymentIdMessage = "Invalid Payment ID " + paymentId;
            throw new InvalidNotificationIdException(invalidPaymentIdMessage);
        }
        Optional<Notification> notification = notificationRepository.findByPaymentId(paymentId);
        return notification.orElseThrow(() -> new NotificationNotFoundException(notFoundMessage));
    }

}
