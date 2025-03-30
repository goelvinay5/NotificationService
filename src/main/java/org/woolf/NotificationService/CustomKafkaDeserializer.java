package org.woolf.NotificationService;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.header.Headers;


public class CustomKafkaDeserializer<T> implements Deserializer<T> {

//    This is DelegatingDeserializer approach.
//    The DelegatingDeserializer allows multiple deserializers to be registered, letting Kafka pick the right one based on the topic.

//    private final Map<String, JsonDeserializer<?>> deserializers = new HashMap<>();
//    public CustomKafkaDeserializer() {
//        deserializers.put("order-completed-topic", new JsonDeserializer<>(OrderCompletedEvent.class));
//        deserializers.put("user-registered-topic", new JsonDeserializer<>(UserRegisteredEvent.class));
//    }

//    @Override
//    public T deserialize(String topic, byte[] data) {
//        JsonDeserializer<?> deserializer = deserializers.get(topic);
//        if (deserializer == null) {
//            throw new RuntimeException("No deserializer found for topic: " + topic);
//        }
//        return (T) deserializer.deserialize(topic, data);
//    }

    private final JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>();

    @Override
    public T deserialize(String s, byte[] bytes) {
        // Fallback method (headers are unavailable here)
        return jsonDeserializer.deserialize(s, bytes);
    }

    @Override
    public T deserialize(String topic,Headers headers, byte[] data) {

        if (headers == null || headers.lastHeader("type") == null) {
            throw new RuntimeException("Missing 'type' header for deserialization");
        }

        String type = new String(headers.lastHeader("type").value()); // Read type from header
        System.out.println("Received type: " + type);

        // ✅ Map short name to correct class
        Class<?> targetClass = switch (type) {
            case "RefundEvent" -> RefundEvent.class;
            case "OrderCompletedEvent" -> OrderCompletedEvent.class;
            default -> throw new RuntimeException("Unknown class type: " + type);
        };

        try {
            JsonDeserializer<T> deserializer = new JsonDeserializer<>((Class<T>) targetClass);
            return deserializer.deserialize(topic, data);
        } catch (Exception e ) {
            throw new RuntimeException("Deserialization error: " +e);
        }
    }
}
