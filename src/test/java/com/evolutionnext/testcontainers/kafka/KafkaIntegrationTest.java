package com.evolutionnext.testcontainers.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class KafkaIntegrationTest {

    @Container
    private static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Test
    void testProduceAndConsumer() throws InterruptedException {
        Properties producerProperties = createProducerProperties();
        Properties consumerProperties = createConsumerProperties();

        KafkaProducer<String, Integer> producer =
            new KafkaProducer<>(producerProperties);

        String topicName = "my_orders";
        int count = 10;

        new FakeCustomerProducer(producer)
            .produceFakeOrderMessages(topicName, count);

        KafkaConsumer<String, Integer> consumer =
            new KafkaConsumer<>(consumerProperties);

        List<Message> messages = FakeCustomerConsumer.consumerFakeOrderMessages(topicName, consumer);

        System.out.println(messages);
        assertThat(messages).size().isEqualTo(10);
        consumer.commitSync(); //Block
        consumer.close();
    }

    @NotNull
    private static Properties createConsumerProperties() {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "marketing");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.IntegerDeserializer");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest");
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
            "false");
        return consumerProperties;
    }

    @NotNull
    private static Properties createProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            IntegerSerializer.class);
        return producerProperties;
    }
}
