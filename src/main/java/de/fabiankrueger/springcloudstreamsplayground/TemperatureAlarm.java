package de.fabiankrueger.springcloudstreamsplayground;

import java.time.Instant;
import java.util.Date;
import lombok.Data;

@Data
public class TemperatureAlarm {
  private Date instant = new Date();
}