package dev.franke.felipe.webhook_cielo_api.api.service;

import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationIdException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
import dev.franke.felipe.webhook_cielo_api.api.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

public class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  private NotificationService notificationService;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    notificationRepository = Mockito.mock(NotificationRepository.class);
    kafkaTemplate = Mockito.mock(KafkaTemplate.class);
    notificationService = new NotificationService(notificationRepository, kafkaTemplate);
  }

  @Test
  @DisplayName(
      "Method 'getNotificationByPaymentId' - Valid PaymentId - Should return a Notification")
  void getNotificationByPaymentId_ValidPaymentId_ShouldReturnNotification() {
    UUID id = UUID.randomUUID();
    String paymentIdUserEntered = id.toString();
    Notification notification = new Notification(paymentIdUserEntered, null, 1);
    Optional<Notification> optionalNotification = Optional.of(notification);
    Mockito.when(notificationRepository.findByPaymentId(paymentIdUserEntered))
        .thenReturn(optionalNotification);
    Notification result = notificationService.getNotificationByPaymentId(paymentIdUserEntered);
    Assertions.assertNotNull(result, () -> "The notification should not be null");
    Assertions.assertEquals(
        result, notification, () -> "The notification should be the same as the mock");
  }

  @Test
  @DisplayName(
      "Method 'getNotificationByPaymentId' - Valid PaymentId/Not Found - Should throw NotificationNotFoundException")
  void
      getNotificationByPaymentId_ValidPaymentIdNotFound_ShouldThrowNotificationNotFoundException() {
    UUID id = UUID.randomUUID();
    String paymentIdUserEntered = id.toString();
    String expectedMessage = "Notification with payment ID " + paymentIdUserEntered + " not found";
    Mockito.when(notificationRepository.findByPaymentId(paymentIdUserEntered))
        .thenThrow(new NotificationNotFoundException(expectedMessage));
    NotificationNotFoundException notFoundException =
        Assertions.assertThrows(
            NotificationNotFoundException.class,
            () -> notificationService.getNotificationByPaymentId(paymentIdUserEntered),
            () ->
                "The method should throw NotificationNotFoundException when the notification is not found");
    Assertions.assertEquals(
        "Notification with payment ID " + paymentIdUserEntered + " not found",
        notFoundException.getMessage(),
        () -> "Expected " + expectedMessage + " but got " + notFoundException.getMessage());
  }

  @Test
  @DisplayName(
      "Method 'getNotificationByPaymentId' - Invalid PaymentId - Should throw InvalidNotificationIdException")
  void getNotificationByPaymentId_InvalidPaymentId_ShouldThrowInvalidNotificationIdException() {
    String paymentIdUserEntered = "invalid-id";
    String expectedMessage = "Invalid Payment ID " + paymentIdUserEntered;
    InvalidNotificationIdException invalidIdException =
        Assertions.assertThrows(
            InvalidNotificationIdException.class,
            () -> notificationService.getNotificationByPaymentId(paymentIdUserEntered),
            () ->
                "The method should throw InvalidNotificationIdException when the paymentId is invalid");
    Assertions.assertEquals(
        "Invalid Payment ID " + paymentIdUserEntered,
        invalidIdException.getMessage(),
        () -> "Expected " + expectedMessage + " but got " + invalidIdException.getMessage());
  }

  @Test
  @DisplayName("Method 'isNotificationSaved' - Notification Saved - Should return true")
  void isNotificationSaved_NotificationSaved_ShouldReturnTrue() {
    String theID = UUID.randomUUID().toString();
    NotificationRequestDTO notificationRequest = new NotificationRequestDTO(theID, null, 1);
    Notification notification = Notification.fromRequest(notificationRequest);
    Mockito.when(notificationRepository.save(notification)).thenReturn(notification);
    boolean result = notificationService.isNotificationSaved(notificationRequest);
    Assertions.assertTrue(result, () -> "The notification should be saved");
    Mockito.verify(notificationRepository).save(Mockito.any());
    Mockito.verify(kafkaTemplate).send("webhook-cielo", "paymentId", theID);
  }

  @Test
  @DisplayName(
      "Method 'isNotificationSaved' - Fail to Save/notificationRepository.save - Should return false")
  void isNotificationSaved_NotificationFails1_ShouldReturnFalse() {
    String theID = UUID.randomUUID().toString();
    NotificationRequestDTO notificationRequest = new NotificationRequestDTO(theID, null, 1);
    Notification notification = Notification.fromRequest(notificationRequest);
    Mockito.when(notificationRepository.save(notification))
        .thenThrow(IllegalArgumentException.class);
    boolean result = notificationService.isNotificationSaved(notificationRequest);
    Assertions.assertFalse(result);
    Mockito.verify(notificationRepository).save(notification);
    Mockito.verify(kafkaTemplate, Mockito.never()).send("webhook-cielo", "paymentId", theID);
  }

  @Test
  @DisplayName(
      "Method 'isNotificationSaved' - Fail to SendKafka/kafkaTemplate.send - Should return false")
  void isNotificationSaved_NotificationFails2_ShouldReturnFalse() {
    String theID = UUID.randomUUID().toString();
    NotificationRequestDTO notificationRequest = new NotificationRequestDTO(theID, null, 1);
    Notification notification = Notification.fromRequest(notificationRequest);
    Mockito.when(kafkaTemplate.send("webhook-cielo", "paymentId", theID))
        .thenAnswer(
            invocation -> {
              throw new Exception();
            });
    boolean result = notificationService.isNotificationSaved(notificationRequest);
    Assertions.assertFalse(result);
    Mockito.verify(notificationRepository).save(notification);
    Mockito.verify(kafkaTemplate).send("webhook-cielo", "paymentId", theID);
  }

  @Test
  @DisplayName(
      "Method 'isNotificationSaved' - Invalid PaymentId - Should throw InvalidNotificationException")
  void isNotificationSaved_NotificationFails3_ShouldThrow() {
    String theID = "invalid";
    NotificationRequestDTO notificationRequest = new NotificationRequestDTO(theID, null, 1);
    InvalidNotificationException exception =
        Assertions.assertThrows(
            InvalidNotificationException.class,
            () -> {
              notificationService.isNotificationSaved(notificationRequest);
            });
    Assertions.assertEquals(
        "Invalid payload: PaymentId/RecurrentPaymentId should be UUID", exception.getMessage());
    Mockito.verify(kafkaTemplate, Mockito.never()).send("webhook-cielo", "paymentId", theID);
  }

  @Test
  @DisplayName(
      "Method 'isNotificationSaved' - Invalid RecurrentPaymentId - Should throw InvalidNotificationException")
  void isNotificationSaved_NotificationFails4_ShouldThrow() {
    String theID = UUID.randomUUID().toString();
    String invalidRecurrentPaymentId = "invalid";
    NotificationRequestDTO notificationRequest =
        new NotificationRequestDTO(theID, invalidRecurrentPaymentId, 1);
    InvalidNotificationException exception =
        Assertions.assertThrows(
            InvalidNotificationException.class,
            () -> {
              notificationService.isNotificationSaved(notificationRequest);
            });
    Assertions.assertEquals(
        "Invalid payload: PaymentId/RecurrentPaymentId should be UUID", exception.getMessage());
    Mockito.verify(kafkaTemplate, Mockito.never()).send("webhook-cielo", "paymentId", theID);
  }

  @Test
  @DisplayName(
      "Method 'isNotificationSaved' - PaymentId null - Should throw InvalidNotificationException")
  void isNotificationSaved_NotificationFails5_ShouldThrow() {
    NotificationRequestDTO notificationRequest = new NotificationRequestDTO(null, null, null);
    InvalidNotificationException exception =
        Assertions.assertThrows(
            InvalidNotificationException.class,
            () -> {
              notificationService.isNotificationSaved(notificationRequest);
            });
    Assertions.assertEquals("Invalid payload: PaymentId is required", exception.getMessage());
    Mockito.verify(kafkaTemplate, Mockito.never()).send("webhook-cielo", "paymentId", null);
  }
}
