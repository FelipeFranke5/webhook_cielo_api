package dev.franke.felipe.webhook_cielo_api.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationRequestDTO(
        @JsonProperty("PaymentId") String paymentId,
        @JsonProperty("RecurrentPaymentId") String recurrentPaymentId,
        @JsonProperty("ChangeType") Integer changeType) {}
