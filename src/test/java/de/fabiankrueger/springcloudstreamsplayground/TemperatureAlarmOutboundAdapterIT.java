package de.fabiankrueger.springcloudstreamsplayground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
    "spring.cloud.stream.bindings.temperatureAlarm.destination=" + TemperatureAlarmOutboundAdapterIT.TOPIC})
@EmbeddedKafka
public class TemperatureAlarmOutboundAdapterIT {

  static final String TOPIC = "temperature-alarm";
  @Autowired
  TemperatureAlarmOutboundAdapter temperatureAlarmOutboundAdapter;
  TemperatureAlarm receivedTemperatureAlarm;

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
}
