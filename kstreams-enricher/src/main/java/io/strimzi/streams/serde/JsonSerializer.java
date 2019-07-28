package io.strimzi.streams.serde;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * JsonSerializer
 */
public class JsonSerializer<T> implements Serializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer() {

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            return this.objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SerializationException(e);
		}
    }

    @Override
    public void close() {
		
	}
}