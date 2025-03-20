package dev.franke.felipe.webhook_cielo_api.api.exception_handler;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseErrorDetail(@JsonProperty("Code") int code, @JsonProperty("Detail") String detail) {}
