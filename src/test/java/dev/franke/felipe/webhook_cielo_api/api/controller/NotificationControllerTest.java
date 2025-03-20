package dev.franke.felipe.webhook_cielo_api.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.dto.response.NotificationResponseDTO;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import dev.franke.felipe.webhook_cielo_api.api.exception.NotificationNotFoundException;
import dev.franke.felipe.webhook_cielo_api.api.exception_handler.NotificationErrorCode;
import dev.franke.felipe.webhook_cielo_api.api.model.Notification;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private NotificationService notificationService;

    private final String URI = "/api/webhook";

    @Test
    @DisplayName("Method 'processData' - Valid data - Should Return OK")
    void processData_ValidData_ShouldReturnOK() throws Exception {
        NotificationRequestDTO payload = new NotificationRequestDTO(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), 1);
        Mockito.when(notificationService.isNotificationSaved(payload)).thenReturn(true);
        mockMvc.perform(post(URI)
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
        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method 'processData' - PaymentId/invalid - Should Return BadRequest")
    void processData_InvalidPaymentId_ShouldReturnBadRequest() throws Exception {
        NotificationRequestDTO payload = new NotificationRequestDTO("invalid", null, 1);
        Mockito.when(notificationService.isNotificationSaved(payload))
                .thenThrow(new InvalidNotificationException(
                        "Invalid payload: PaymentId/RecurrentPaymentId should be UUID"));
        mockMvc.perform(post(URI)
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
                .thenThrow(new InvalidNotificationException(
                        "Invalid payload: PaymentId/RecurrentPaymentId should be UUID"));
        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method 'retrieveData' - Valid Data/not found - Returns 404")
    void retrieveData_ValidDataNotFound_ShouldReturn404() throws Exception {
        String theID = UUID.randomUUID().toString();
        String notFoundMessage = "Notification with Payment ID " + theID + " not found";
        Mockito.when(notificationService.getNotificationByPaymentId(theID))
                .thenThrow(new NotificationNotFoundException(notFoundMessage));
        mockMvc.perform(get(URI + "/" + theID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Code").value(NotificationErrorCode.PAYMENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.Detail").value(notFoundMessage));
    }

    @Test
    @DisplayName("Method 'retrieveData' - Valid Data/found - Returns NotificationResponseDTO")
    void retrieveData_ValidDataFound_ShouldReturnNotificationResponseDTO() throws Exception {
        String theID = UUID.randomUUID().toString();
        String recurrent = UUID.randomUUID().toString();
        int changeType = 4;
        Notification notification = new Notification(theID, recurrent, changeType);
        NotificationResponseDTO responseBody = new NotificationResponseDTO(
                notification.getPaymentId(), notification.getRecurrentPaymentId(), notification.getChangeType());
        Mockito.when(notificationService.getNotificationByPaymentId(theID)).thenReturn(notification);
        mockMvc.perform(get(URI + "/" + theID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PaymentId").value(responseBody.paymentId()))
                .andExpect(jsonPath("$.RecurrentPaymentId").value(responseBody.recurrentPaymentId()))
                .andExpect(jsonPath("$.ChangeType").value(responseBody.changeType()));
    }
}
