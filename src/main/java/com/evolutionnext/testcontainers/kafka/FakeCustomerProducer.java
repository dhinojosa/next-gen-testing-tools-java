package com.evolutionnext.testcontainers.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Random;

public class FakeCustomerProducer {
    private final KafkaProducer<String, Integer> producer;
    private final Random random;

    public FakeCustomerProducer(KafkaProducer<String, Integer> producer) {
        this.producer = producer;
        this.random = new Random();
    }

    void produceFakeOrderMessages(String topicName, int count) {
        String stateString =
            "AK,AL,AZ,AR,CA,CO,CT,DE,FL,GA," +
                "HI,ID,IL,IN,IA,KS,KY,LA,ME,MD," +
                "MA,MI,MN,MS,MO,MT,NE,NV,NH,NJ," +
                "NM,NY,NC,ND,OH,OK,OR,PA,RI,SC," +
                "SD,TN,TX,UT,VT,VA,WA,WV,WI,WY";


        for (int i = 0; i < count; i++) {
            String[] states = stateString.split(",");
            String state = states[random.nextInt(states.length)];
            int amount = random.nextInt(100000 - 50 + 1) + 50;

            ProducerRecord<String, Integer> producerRecord =
                new ProducerRecord<>(topicName, state, amount);

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
    }
}
