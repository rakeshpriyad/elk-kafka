package com.d2d.producer;

import com.d2d.types.Employee;
import com.d2d.utils.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmployeeProducer {
    private static final Logger logger = LogManager.getLogger(EmployeeProducer.class);

    public static void main(String[] args) {
        String topicName;
        int numEvents;
        topicName = "employees";
        numEvents = 20;
        logger.info("Starting HelloProducer...");
        logger.debug("topicName=" + topicName + ", numEvents=" + numEvents);

        logger.trace("Creating Kafka Producer...");


        KafkaProducer<String, Employee> producer = new KafkaProducer<>(Configuration.getProducerConfig1());

        logger.trace("Start sending messages...");
        try {
            for (int i = 1; i <= numEvents; i++) {
                Employee e = new Employee();
                e.setId("1"+i);
                e.setDepartment("d1");
                e.setName("Test");
                e.setSalary(2000000);
                producer.send(new ProducerRecord<String, Employee>(topicName, ""+i, e));
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
