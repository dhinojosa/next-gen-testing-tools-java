package com.evolutionnext.testcontainers.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class KafkaIntegrationTest {

    @Container
    private static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Test
    void testProduceAndConsumer() throws InterruptedException {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            IntegerSerializer.class);

        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafka.getBootstrapServers());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "marketing");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.IntegerDeserializer");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaProducer<String, Integer> producer =
            new KafkaProducer<>(producerProperties);

        String stateString =
            "AK,AL,AZ,AR,CA,CO,CT,DE,FL,GA," +
                "HI,ID,IL,IN,IA,KS,KY,LA,ME,MD," +
                "MA,MI,MN,MS,MO,MT,NE,NV,NH,NJ," +
                "NM,NY,NC,ND,OH,OK,OR,PA,RI,SC," +
                "SD,TN,TX,UT,VT,VA,WA,WV,WI,WY";

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String[] states = stateString.split(",");
            String state = states[random.nextInt(states.length)];
            int amount = random.nextInt(100000 - 50 + 1) + 50;

            ProducerRecord<String, Integer> producerRecord =
                new ProducerRecord<>("my_orders", state, amount);

            //Asynchronous
            producer.send(producerRecord, (metadata, e) -> {
                if (metadata != null) {
                    System.out.println(producerRecord.key());
                    System.out.println(producerRecord.value());

                    if (metadata.hasOffset()) {
                        System.out.format("offset: %d\n",
                            metadata.offset());
                    }
                    System.out.format("partition: %d\n",
                        metadata.partition());
                    System.out.format("timestamp: %d\n",
                        metadata.timestamp());
                    System.out.format("topic: %s\n", metadata.topic());
                    System.out.format("toString: %s\n",
                        metadata.toString());
                } else {
                    System.out.println("ERROR! ");
                    String firstException =
                        Arrays.stream(e.getStackTrace())
                              .findFirst()
                              .map(StackTraceElement::toString)
                              .orElse("Undefined Exception");
                    System.out.println(firstException);
                }
            });
        }


        KafkaConsumer<String, Integer> consumer =
            new KafkaConsumer<>(consumerProperties);

        consumer.subscribe(Collections.singletonList("my_orders"),
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

        record Message(String key, Integer value){}
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ConsumerRecords<String, Integer> records =
                consumer.poll(Duration.of(500, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, Integer> record : records) {
                messages.add(new Message(record.key(), record.value()));
            }
        }

        System.out.println(messages);
        assertThat(messages).size().isEqualTo(10);
        consumer.commitSync(); //Block
        consumer.close();
    }


    private static String collectionTopicPartitionToString
        (Collection<TopicPartition> topicPartitions) {
        return topicPartitions.stream()
                              .map(tp -> tp.topic() + " - " + tp.partition())
                              .collect(Collectors.joining(","));
    }
}
