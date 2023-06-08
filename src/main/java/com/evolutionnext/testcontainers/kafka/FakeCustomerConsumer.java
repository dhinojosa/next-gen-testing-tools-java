package com.evolutionnext.testcontainers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FakeCustomerConsumer {
    private static KafkaConsumer<String, Integer> consumer;

    static String collectionTopicPartitionToString
        (Collection<TopicPartition> topicPartitions) {
        return topicPartitions.stream()
                              .map(tp -> tp.topic() + " - " + tp.partition())
                              .collect(Collectors.joining(","));
    }

    static List<Message> consumerFakeOrderMessages(String topicName,
                                                   KafkaConsumer<String,
                                                       Integer> consumer) {
        FakeCustomerConsumer.consumer = consumer;
        consumer.subscribe(Collections.singletonList(topicName),
            new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                    System.out.println("Partition revoked:" +
                        collectionTopicPartitionToString(collection));
                    consumer.commitAsync();
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                    System.out.println("Partition assigned:" +
                        collectionTopicPartitionToString(collection));
                }
            });

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ConsumerRecords<String, Integer> records =
                consumer.poll(Duration.of(500, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, Integer> record : records) {
                messages.add(new Message(record.key(), record.value()));
            }
        }
        return messages;
    }
}
