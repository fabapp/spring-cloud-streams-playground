package de.fabiankrueger.springcloudstreamsplayground;

import java.time.Instant;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class TemperatureAlarm {
  @Id
  @GeneratedValue
  private Long id;
  private int temperature;
  private Date instant = new Date();
}
