package dev.franke.felipe.webhook_cielo_api.api.model;

import dev.franke.felipe.webhook_cielo_api.api.dto.request.NotificationRequestDTO;
import dev.franke.felipe.webhook_cielo_api.api.exception.InvalidNotificationException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {

    public static Notification fromRequest(NotificationRequestDTO request) {
        try {
            String paymentId = request.paymentId();
            String recurrent = request.recurrentPaymentId();
            if (paymentId != null) {
                UUID.fromString(paymentId);
            } else {
                throw new InvalidNotificationException("Invalid payload: PaymentId is required");
            }
            if (recurrent != null) {
                UUID.fromString(recurrent);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new InvalidNotificationException("Invalid payload: PaymentId/RecurrentPaymentId should be UUID");
        }
        return new Notification(request.paymentId(), request.recurrentPaymentId(), request.changeType());
    }

    @Id
    private String paymentId;

    private String recurrentPaymentId;
    private Integer changeType;

    public Notification() {}

    public Notification(String paymentId, String recurrentPaymentId, int changeType) {
        this.paymentId = paymentId;
        this.recurrentPaymentId = recurrentPaymentId;
        this.changeType = changeType;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRecurrentPaymentId() {
        return recurrentPaymentId;
    }

    public void setRecurrentPaymentId(String recurrentPaymentId) {
        this.recurrentPaymentId = recurrentPaymentId;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
        result = prime * result + ((recurrentPaymentId == null) ? 0 : recurrentPaymentId.hashCode());
        result = prime * result + changeType;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Notification other = (Notification) obj;
        if (paymentId == null) {
            if (other.paymentId != null) return false;
        } else if (!paymentId.equals(other.paymentId)) return false;
        if (recurrentPaymentId == null) {
            if (other.recurrentPaymentId != null) return false;
        } else if (!recurrentPaymentId.equals(other.recurrentPaymentId)) return false;
        return Objects.equals(changeType, other.changeType);
    }

    @Override
    public String toString() {
        return "Notification [paymentId="
                + paymentId
                + ", recurrentPaymentId="
                + recurrentPaymentId
                + ", changeType="
                + changeType
                + "]";
    }
}
