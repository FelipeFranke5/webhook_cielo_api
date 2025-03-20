package dev.franke.felipe.webhook_cielo_api.api.controller;

import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping
  public ResponseEntity<Void> processData(@RequestBody NotificationRequestDTO payload) {
    return notificationService.isNotificationSaved(payload) ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
  }
}
