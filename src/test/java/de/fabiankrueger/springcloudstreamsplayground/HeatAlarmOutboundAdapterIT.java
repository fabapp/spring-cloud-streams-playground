package de.fabiankrueger.springcloudstreamsplayground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
    "spring.cloud.stream.bindings.heatAlarm.destination=heat-alarm"})
@EmbeddedKafka
public class HeatAlarmOutboundAdapterIT {

  @Autowired
  HeatAlarmOutboundAdapter heatAlarmOutboundAdapter;

//  CountDownLatch heatAlarmMessageReceivedLatch = new CountDownLatch(1);
  HeatAlarm receivedHeatAlarm;

  @Test
  public void sendHeatAlarm() throws InterruptedException {
    HeatAlarm heatAlarm = new HeatAlarm();
    heatAlarmOutboundAdapter.send(heatAlarm);
    await().until(() -> receivedHeatAlarm!=null);
//    heatAlarmMessageReceivedLatch.await();
    assertThat(receivedHeatAlarm.getInstant()).isEqualTo(heatAlarm.getInstant());
  }

  @KafkaListener(topics = {"heat-alarm"}, groupId = "test-consumer")
  public void onHeatAlarm(GenericMessage heatAlarmMessage) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    receivedHeatAlarm = objectMapper.readValue((byte[]) heatAlarmMessage.getPayload(), HeatAlarm.class);
//    heatAlarmMessageReceivedLatch.countDown();
  }
}
