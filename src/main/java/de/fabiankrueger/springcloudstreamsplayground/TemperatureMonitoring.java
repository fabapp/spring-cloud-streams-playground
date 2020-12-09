package de.fabiankrueger.springcloudstreamsplayground;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemperatureMonitoring {

  private final TemperatureAlarmRepository alarmRepository;
  private final TemperatureAlarmOutboundAdapter alarmOutboundAdapter;
  private final ProblemInjection problemInjection;

  @Transactional(rollbackFor = RuntimeException.class)
  public void monitor(TemperatureMeasurement measurement) {
    if(measurement.getDegreeCelsius() > 10) {
      TemperatureAlarm temperatureAlarm = new TemperatureAlarm();
      temperatureAlarm.setTemperature(measurement.getDegreeCelsius());
      alarmRepository.save(temperatureAlarm);
      alarmOutboundAdapter.send(temperatureAlarm);
      problemInjection.beforeReturning();
    }
  }
}
