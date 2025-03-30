package org.woolf.NotificationService;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    //Configures Kafka consumer properties (deserialization, session timeout, etc.)
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        //props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //Ensures consumer starts reading messages from the earliest offset if no previous offset exists
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 25000); //Sets session timeout (to handle consumer failures)

        // ✅ Use ErrorHandlingDeserializer to prevent crashes due to deserialization issues
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // ✅ Delegating deserializers (actual deserialization logic)
        props.put("spring.deserializer.key.delegate.class", StringDeserializer.class.getName());
        props.put("spring.deserializer.value.delegate.class", CustomKafkaDeserializer.class.getName());

        // Required for JSON deserialization
//        JsonDeserializer<OrderCompletedEvent> deserializer = new JsonDeserializer<>(OrderCompletedEvent.class);
//        deserializer.addTrustedPackages("*"); // Allows deserialization of any package

        return new DefaultKafkaConsumerFactory<>(props);
    }

    //This method creates a Kafka listener factory, which Spring Boot uses to manage Kafka message consumption.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setMissingTopicsFatal(false); // Prevent errors if topic is missing

        // ✅ Redirect failed messages to error-topic instead of skipping
        factory.setCommonErrorHandler(new DefaultErrorHandler(new DeadLetterPublishingRecoverer(this.kafkaTemplate())));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    /*
    *
    *  How These Beans Work Together?
Flow:
1️⃣ consumerFactory() configures Kafka consumers (including deserialization).
2️⃣ kafkaListenerContainerFactory() uses consumerFactory() to build a listener.
3️⃣ @KafkaListener picks up messages and processes them.
*
* */
}
