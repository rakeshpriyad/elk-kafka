
package com.d2d.consumer;

import com.d2d.utils.Configuration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class HelloStreamsWithProcessor {
    private static final Logger logger = LogManager.getLogger();
    private static final String topicName = "hello-producer";

    public static void main(String[] args) {
        Properties props = Configuration.getConsumerConfig("HelloStream");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<Integer, String> kStream = builder.stream(topicName);
        kStream.process(()-> new HelloProcessor());
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        //Either use like below or above
        /*final Topology topology = new Topology();
       // topology.addSource("Source", topicName);
        topology.addProcessor("Process", ()-> new HelloProcessor(), "Source");
        final KafkaStreams streams = new KafkaStreams(topology, props);
        */


        logger.info("Starting the stream");
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Stream");
            streams.close();
        }));
    }
}
