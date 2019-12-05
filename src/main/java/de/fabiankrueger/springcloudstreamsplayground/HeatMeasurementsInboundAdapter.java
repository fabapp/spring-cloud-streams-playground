package de.fabiankrueger.springcloudstreamsplayground;


import static de.fabiankrueger.springcloudstreamsplayground.HeatMeasurementsInboundAdapter.HeatMeasurementsBinder.HEAT_MEASUREMENTS;

import de.fabiankrueger.springcloudstreamsplayground.HeatMeasurementsInboundAdapter.HeatMeasurementsBinder;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(HeatMeasurementsBinder.class)
@AllArgsConstructor
public class HeatMeasurementsInboundAdapter {

  private final HeatMonitoring heatMonitoring;

  public interface  HeatMeasurementsBinder {

    public static String HEAT_MEASUREMENTS = "heatMeasurements";

    @Input(HEAT_MEASUREMENTS)
    SubscribableChannel input();
  }

  @StreamListener(HEAT_MEASUREMENTS)
  public void onHeatMeasuremet(Message<HeatMeasurement> heatMeasurementMessage) {
    heatMonitoring.monitor(heatMeasurementMessage.getPayload());
  }

}
