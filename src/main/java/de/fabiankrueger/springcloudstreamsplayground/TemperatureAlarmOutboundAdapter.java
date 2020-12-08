package de.fabiankrueger.springcloudstreamsplayground;

import de.fabiankrueger.springcloudstreamsplayground.TemperatureAlarmOutboundAdapter.TemperatureAlarmBinder;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@EnableBinding(TemperatureAlarmBinder.class)
public class TemperatureAlarmOutboundAdapter {

  public interface TemperatureAlarmBinder {
    String TERMPERATURE_ALARM = "temperatureAlarm";
    @Output(TERMPERATURE_ALARM)
    MessageChannel channel();
  }

  private final TemperatureAlarmBinder temperatureAlarmBinder;

  public boolean send(TemperatureAlarm temperatureAlarm) {
    return temperatureAlarmBinder.channel().send(MessageBuilder.withPayload(temperatureAlarm).build());
  }

}
