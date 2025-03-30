package org.woolf.NotificationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = "order-completed-topic", groupId = "notification-group")
    public void listen(OrderCompletedEvent event) {
        System.out.println("Received order completed event: " + event.toString());
        sendEmail(event.getCustomerEmail(), "Order Completed", event.getMessage());
    }


    @KafkaListener(topics = "refund-initiated-topic", groupId = "notification-group")
    public void listen(RefundEvent event) {
        System.out.println("Received order cancelled event: " + event.toString());
        sendEmail(event.getCustomerEmail(), "Refund Initiated", event.getMessage());
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
            System.out.println("Email sent successfully to " +to);
        } catch (MailException e) {
            System.out.println("Failed to send email:"+ e.getMessage());
        }
    }
}
