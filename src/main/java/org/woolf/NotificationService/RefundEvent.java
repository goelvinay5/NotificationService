package org.woolf.NotificationService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefundEvent {
    private String orderId;
    private String customerEmail;
    private String message;
    private String refundAmount;

    @Override
    public String toString() {
        return "RefundEvent{" +
                "orderId='" + orderId + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", message='" + message + '\'' +
                ", refundAmount='" + refundAmount + '\'' +
                '}';
    }
}
