
package com.d2d;

import com.d2d.types.DepartmentAggregate;
import com.d2d.types.Employee;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;


import java.util.Properties;


public class KStreamAggDemo {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfigs.bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, AppConfigs.stateStoreName);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Employee> KS0 = streamsBuilder.stream(AppConfigs.topicName,
            Consumed.with(AppSerdes.String(), AppSerdes.Employee()));

        KGroupedStream<String, Employee> KGS1 = KS0.groupBy( (k, v) -> v.getDepartment(),
            Serialized.with(AppSerdes.String(),
                AppSerdes.Employee()));

        KTable<String, DepartmentAggregate> KT2 = KGS1.aggregate(
            //Initializer
            () -> new DepartmentAggregate()
                .withEmployeeCount(0)
                .withTotalSalary(0)
                .withAvgSalary(0D),
            //Aggregator
            (k, v, aggV) -> new DepartmentAggregate()
                .withEmployeeCount(aggV.getEmployeeCount() + 1)
                .withTotalSalary(aggV.getTotalSalary() + v.getSalary())
                .withAvgSalary((aggV.getTotalSalary() + v.getSalary()) / (aggV.getEmployeeCount() + 1D)),
            //Serializer
            Materialized.<String, DepartmentAggregate, KeyValueStore<Bytes, byte[]>>as("agg-store")
                .withKeySerde(AppSerdes.String())
                .withValueSerde(AppSerdes.DepartmentAggregate())
        );

        KT2.toStream().foreach(
            (k, v) -> System.out.println("Key = " + k + " Value = " + v.toString()));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
