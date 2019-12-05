package de.fabiankrueger.springcloudstreamsplayground;

import de.fabiankrueger.springcloudstreamsplayground.HeatAlarmOutboundAdapter.HeatAlarmBinder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@EnableBinding(HeatAlarmBinder.class)
public class HeatAlarmOutboundAdapter {

  public interface HeatAlarmBinder {
    String HEAT_ALARM = "heatAlarm";
    @Output(HEAT_ALARM)
    MessageChannel channel();
  }

  private final HeatAlarmBinder heatAlarmBinder;

  public void send(HeatAlarm heatAlarm) {
    heatAlarmBinder.channel().send(MessageBuilder.withPayload(heatAlarm).build());
  }

}
