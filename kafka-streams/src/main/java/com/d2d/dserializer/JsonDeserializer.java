package com.d2d.dserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> className;

    public JsonDeserializer() {

    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        className = (Class<T>) props.get("specific.class.name");
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, className);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
        //nothing to close
    }
}
