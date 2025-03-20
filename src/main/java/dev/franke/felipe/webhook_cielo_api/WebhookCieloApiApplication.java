package dev.franke.felipe.webhook_cielo_api;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class WebhookCieloApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(WebhookCieloApiApplication.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(WebhookCieloApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner initial() {
        return args -> {
            logger.info("\n\n---------------------- TESTING KAFKA SEND ..");
            kafkaTemplate.send("webhook-cielo", "paymentId", UUID.randomUUID().toString());
            kafkaTemplate.send("webhook-cielo", "paymentId", UUID.randomUUID().toString());
            logger.info("\n\n---------------------- END OF KAFKA SEND ..");
        };
    }
}
