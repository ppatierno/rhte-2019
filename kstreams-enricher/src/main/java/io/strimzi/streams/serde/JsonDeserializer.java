package io.strimzi.streams.serde;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

/**
 * JsonDeserializer
 */
public class JsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> type;

    public JsonDeserializer() {
        
    }

    public JsonDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        T data = null;
        try {
            data = objectMapper.readValue(bytes, this.type);
        } catch (Exception e) {
            e.printStackTrace();
		}
        return data;
    }

    @Override
    public void close() {
		
	}
}