package dev.franke.felipe.webhook_cielo_api.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import dev.franke.felipe.webhook_cielo_api.api.service.NotificationService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private KafkaTemplate<String, String> kafkaTemplate;

  @MockitoBean private KafkaAdmin kafkaAdmin;

  @MockitoBean private NotificationService notificationService;

  private final String URI = "/api/webhook";

  @Test
  @DisplayName("Method 'processData' - Valid data - Should Return OK")
  void processData_ValidData_ShouldReturnOK() throws Exception {
    NotificationRequestDTO payload =
        new NotificationRequestDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 1);
    Mockito.when(notificationService.isNotificationSaved(payload)).thenReturn(true);
    mockMvc
        .perform(
            post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Method 'processData' - PaymentId/null - Should Return BadRequest")
  void processData_PaymentIdNull_ShouldReturnBadRequest() throws Exception {
    NotificationRequestDTO payload = new NotificationRequestDTO(null, null, null);
    Mockito.when(notificationService.isNotificationSaved(payload))
        .thenThrow(new InvalidNotificationException("Invalid payload: PaymentId is required"));
    mockMvc
        .perform(
            post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Method 'processData' - PaymentId/invalid - Should Return BadRequest")
  void processData_InvalidPaymentId_ShouldReturnBadRequest() throws Exception {
    NotificationRequestDTO payload = new NotificationRequestDTO("invalid", null, 1);
    Mockito.when(notificationService.isNotificationSaved(payload))
        .thenThrow(
            new InvalidNotificationException(
                "Invalid payload: PaymentId/RecurrentPaymentId should be UUID"));
    mockMvc
        .perform(
            post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Method 'processData' - RecurrentPaymentId/invalid - Should Return BadRequest")
  void processData_InvalidRecurrentPaymentId_ShouldReturnBadRequest() throws Exception {
    NotificationRequestDTO payload =
        new NotificationRequestDTO(UUID.randomUUID().toString(), "invalid", 1);
    Mockito.when(notificationService.isNotificationSaved(payload))
        .thenThrow(
            new InvalidNotificationException(
                "Invalid payload: PaymentId/RecurrentPaymentId should be UUID"));
    mockMvc
        .perform(
            post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest());
  }
}
