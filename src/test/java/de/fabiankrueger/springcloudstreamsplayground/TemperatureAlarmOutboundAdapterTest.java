package de.fabiankrueger.springcloudstreamsplayground;


import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.matcher.MessageQueueMatcher;
import org.springframework.messaging.Message;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.Matchers.contains;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TemperatureAlarmOutboundAdapterTest {

    @Autowired
    MessageCollector messageCollector;

    @Autowired
    TemperatureAlarmOutboundAdapter.TemperatureAlarmBinder temperatureAlarmBinder;

    @Autowired
    TemperatureAlarmOutboundAdapter temperatureAlarmOutboundAdapter;

    @Test
    public void test() {
        TemperatureAlarm temperatureAlarm = new TemperatureAlarm();
        Date instant = new Date();
        temperatureAlarm.setInstant(instant);
        temperatureAlarmOutboundAdapter.send(temperatureAlarm);
        final Message<?> message = messageCollector.forChannel(temperatureAlarmBinder.channel()).poll();
        MessageQueueMatcher.receivesPayloadThat(contains(instant));
    }
}
