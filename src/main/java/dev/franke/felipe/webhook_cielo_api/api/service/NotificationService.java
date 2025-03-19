package dev.franke.felipe.webhook_cielo_api.api.service;

import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public NotificationService(
            NotificationRepository notificationRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.notificationRepository = notificationRepository;
        this.kafkaTemplate = kafkaTemplate;
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

    public boolean isNotificationSaved(NotificationRequestDTO notificationRequestDTO) {
        notificationRepository.save(Notification.fromRequest(notificationRequestDTO));
        kafkaTemplate.send("webhook-cielo", "paymentId", notificationRequestDTO.paymentId());
        return true;
    }

}
