package de.fabiankrueger.springcloudstreamsplayground;


import static de.fabiankrueger.springcloudstreamsplayground.HeatMeasurementsInboundAdapter.TemperatureMeasurementsBinder.TEMPERATURE_MEASUREMENTS;

import de.fabiankrueger.springcloudstreamsplayground.HeatMeasurementsInboundAdapter.TemperatureMeasurementsBinder;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(TemperatureMeasurementsBinder.class)
@AllArgsConstructor
public class HeatMeasurementsInboundAdapter {

  private final TemperatureMonitoring temperatureMonitoring;

  public interface TemperatureMeasurementsBinder {

    public static String TEMPERATURE_MEASUREMENTS = "temperatureMeasurements";

    @Input(TEMPERATURE_MEASUREMENTS)
    SubscribableChannel input();
  }

  @StreamListener(TEMPERATURE_MEASUREMENTS)
  public void onHeatMeasuremet(Message<TemperatureMeasurement> heatMeasurementMessage) {
    temperatureMonitoring.monitor(heatMeasurementMessage.getPayload());
  }

}
