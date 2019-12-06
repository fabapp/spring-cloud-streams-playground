package de.fabiankrueger.springcloudstreamsplayground;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
    "spring.cloud.stream.bindings.temperatureMeasurements.destination=" + TemperatureMeasurementsInboundAdapterIT.TOPIC})
@EmbeddedKafka
public class TemperatureMeasurementsInboundAdapterIT {

  static final String TOPIC = "temperature-measurements";

  @Autowired
  KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;

  @MockBean
  TemperatureMonitoring temperatureMonitoring;

  @Autowired
  EmbeddedKafkaBroker embeddedKafkaBroker;
  static EmbeddedKafkaBroker staticKafkaBroker;

  @BeforeEach
  public void setup() {
    staticKafkaBroker = embeddedKafkaBroker;
  }

  @Test
  public void onInboundHeatMeasurement() throws JsonProcessingException, ExecutionException, InterruptedException {
    TemperatureMeasurement temperatureMeasurement = new TemperatureMeasurement(23);
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] heatMeasurementBytes = objectMapper.writeValueAsBytes(temperatureMeasurement);
    Message<byte[]> message = MessageBuilder
        .withPayload(heatMeasurementBytes)
        .setHeader(KafkaHeaders.TOPIC, TOPIC)
        .build();
    kafkaTemplate.send(message).get();
    await().untilAsserted(() -> verify(temperatureMonitoring).monitor(temperatureMeasurement));
  }

  @AfterAll
  public static void tearDown() {
    staticKafkaBroker.getKafkaServers().forEach(b -> b.shutdown());
    staticKafkaBroker.getKafkaServers().forEach(b -> b.awaitShutdown());
  }
}
