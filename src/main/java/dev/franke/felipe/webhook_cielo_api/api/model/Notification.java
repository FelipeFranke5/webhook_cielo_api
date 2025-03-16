package dev.franke.felipe.webhook_cielo_api.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
* Main class for the Notification model.
* Using JPA annotations to map the class to the db table.
* @author Felipe
* @version 1.0
* @since 2025-03-15
*/
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    private String paymentId;

    private String recurrentPaymentId;
    private int changeType;

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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Notification other = (Notification) obj;
        if (paymentId == null) {
            if (other.paymentId != null)
                return false;
        } else if (!paymentId.equals(other.paymentId))
            return false;
        if (recurrentPaymentId == null) {
            if (other.recurrentPaymentId != null)
                return false;
        } else if (!recurrentPaymentId.equals(other.recurrentPaymentId))
            return false;
        if (changeType != other.changeType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Notification [paymentId=" + paymentId + ", recurrentPaymentId=" + recurrentPaymentId + ", changeType="
                + changeType + "]";
    }

}
