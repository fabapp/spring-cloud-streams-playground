package de.fabiankrueger.springcloudstreamsplayground;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class TransactionalOutboxMessage {
    @Id
    @GeneratedValue
    private Long id;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant sentAt;
    private String destination;
    private Long offset;
    private String key;
    private String headers;
    private String payload;

    public Map<String,?> getHeadersMap() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(headers, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
