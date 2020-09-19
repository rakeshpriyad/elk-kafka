package com.d2d.producer;

import com.d2d.utils.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class HelloProducer {
    private static final Logger logger = LogManager.getLogger(HelloProducer.class);

    public static void main(String[] args) {
        String topicName;
        int numEvents;
        topicName = "hello-producer";
        numEvents = 20;
        logger.info("Starting HelloProducer...");
        logger.debug("topicName=" + topicName + ", numEvents=" + numEvents);

        logger.trace("Creating Kafka Producer...");


        KafkaProducer<Integer, String> producer = new KafkaProducer<>(Configuration.getProducerConfig());

        logger.trace("Start sending messages...");
        try {
            for (int i = 1; i <= numEvents; i++) {
                producer.send(new ProducerRecord<>(topicName, i, "Simple Message-" + i));
            }
        } catch (KafkaException e) {
            logger.error("Exception occurred - Check log for more details.\n" + e.getMessage());
            System.exit(-1);
        } finally {
            logger.info("Finished HelloProducer - Closing Kafka Producer.");
            producer.close();
        }

    }
}
