package com.example.demo.listener;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumer {

    @KafkaListener(topics = "Kafka_Example", group = "group_id")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }


    @KafkaListener(topics = "kafka_Example_json", group = "group_json",
            containerFactory = "userKafkaListenerFactory")
    public void consumeJson(User user) {
        System.out.println("Consumed JSON Message: " + user.toString());
    }
}
