package dev.franke.felipe.webhook_cielo_api.api.controller;

import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.dto.response.NotificationResponseDTO;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> processData(@RequestBody NotificationRequestDTO payload) {
        return notificationService.isNotificationSaved(payload)
                ? ResponseEntity.ok().build()
                : ResponseEntity.internalServerError().build();
    }

    @GetMapping("{paymentId}")
    public ResponseEntity<NotificationResponseDTO> retrieveData(@PathVariable String paymentId) {
        Notification notification = notificationService.getNotificationByPaymentId(paymentId);
        NotificationResponseDTO responseBody = new NotificationResponseDTO(
                notification.getPaymentId(), notification.getRecurrentPaymentId(), notification.getChangeType());
        return ResponseEntity.ok(responseBody);
    }
}
