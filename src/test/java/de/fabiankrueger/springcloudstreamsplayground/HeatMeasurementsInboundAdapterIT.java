package de.fabiankrueger.springcloudstreamsplayground;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
    "spring.cloud.stream.bindings.heatMeasurements.destination=measurements-heat"})
@EmbeddedKafka // (topics = {"measurements-heat"})
public class HeatMeasurementsInboundAdapterIT {

  @Autowired
  KafkaTemplate<String, HeatMeasurement> kafkaTemplate;

  @MockBean
  HeatMonitoring heatMonitoring;

  @Test
  public void onInboundHeatMeasurement() throws JsonProcessingException, ExecutionException, InterruptedException {
    HeatMeasurement heatMeasurement = new HeatMeasurement(23);
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] heatMeasurementBytes = objectMapper.writeValueAsBytes(heatMeasurement);
    Message<byte[]> message = MessageBuilder
        .withPayload(heatMeasurementBytes)
        .setHeader(KafkaHeaders.TOPIC, "measurements-heat")
        .build();
    SendResult<String, HeatMeasurement> stringHeatMeasurementSendResult = kafkaTemplate.send(message).get();
    await().untilAsserted(() -> verify(heatMonitoring).monitor(heatMeasurement));
  }

}
