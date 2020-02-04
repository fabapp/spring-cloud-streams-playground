package de.fabiankrueger.springcloudstreamsplayground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TemperatureMeasurementInboundAdapterTest {
    @Autowired
    HeatMeasurementsInboundAdapter.TemperatureMeasurementsBinder temperatureMeasurementsBinder;

    @MockBean
    TemperatureMonitoring temperatureMonitoring;

    @Test
    public void inboundMessageShouldBeProcessedByTemperatureMonitoring() {
        TemperatureMeasurement measurement = new TemperatureMeasurement();
        measurement.setDegreeCelsius(30);
        Message<TemperatureMeasurement> heatMeasurementMessage = MessageBuilder.withPayload(measurement).build();
        temperatureMeasurementsBinder.input().send(heatMeasurementMessage);
        verify(temperatureMonitoring).monitor(measurement);
    }

}
