package dev.franke.felipe.webhook_cielo_api.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationResponseDTO(
        @JsonProperty("PaymentId") String paymentId,
        @JsonProperty("RecurrentPaymentId") String recurrentPaymentId,
        @JsonProperty("ChangeType") Integer changeType) {}
