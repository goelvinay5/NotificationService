package org.woolf.NotificationService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueConsumer {
    @KafkaListener(topics = "error-topic", groupId = "dlq-notification-group")
    public void consumeFailedMessages(ConsumerRecord<String, String> record) {
        System.err.println(" DLQ received message: " + record.value());
    }
}
