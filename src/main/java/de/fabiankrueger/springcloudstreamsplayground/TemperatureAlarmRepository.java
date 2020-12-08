package de.fabiankrueger.springcloudstreamsplayground;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureAlarmRepository extends JpaRepository<TemperatureAlarm, Long> {
}
