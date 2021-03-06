package de.fabiankrueger.springcloudstreamsplayground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
    "spring.kafka.consumer.auto-offset-reset=earliest", // lets the consumer read messages that might have been sent before consumer started
    "spring.cloud.stream.bindings.temperatureAlarm.destination=" + TemperatureAlarmOutboundAdapterIT.TOPIC,
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka
public class TemperatureAlarmOutboundAdapterIT {

  static final String TOPIC = "temperature-alarm";

  @Autowired
  TemperatureAlarmOutboundAdapter temperatureAlarmOutboundAdapter;

  TemperatureAlarm receivedTemperatureAlarm;

  @Autowired
  EmbeddedKafkaBroker embeddedKafkaBroker;
  static EmbeddedKafkaBroker staticKafkaBroker;

  @BeforeEach
  public void setup() {
    staticKafkaBroker = embeddedKafkaBroker;
  }

  @Test
  public void sendHeatAlarm() throws InterruptedException {
    TemperatureAlarm temperatureAlarm = new TemperatureAlarm();
    temperatureAlarmOutboundAdapter.send(temperatureAlarm);
    await().until(() -> receivedTemperatureAlarm !=null);
    assertThat(receivedTemperatureAlarm.getInstant()).isEqualTo(temperatureAlarm.getInstant());
  }

  @KafkaListener(topics = {TOPIC}, groupId = "test-consumer")
  public void onHeatAlarm(GenericMessage heatAlarmMessage) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    receivedTemperatureAlarm = objectMapper.readValue((byte[]) heatAlarmMessage.getPayload(), TemperatureAlarm.class);
  }

  @AfterAll
  public static void tearDown() {
    staticKafkaBroker.getKafkaServers().forEach(b -> b.shutdown());
    staticKafkaBroker.getKafkaServers().forEach(b -> b.awaitShutdown());
  }
}
