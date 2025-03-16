package dev.franke.felipe.webhook_cielo_api.api.repository;

import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    Notification findByPaymentId(String paymentId);
}
