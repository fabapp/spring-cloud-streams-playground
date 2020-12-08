package de.fabiankrueger.springcloudstreamsplayground;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.support.GenericMessage;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = {
        // "spring.cloud.stream.kafka.bindings.temperatureAlarm.producer.configuration.retries=3", // didn't work ?!
        "spring.cloud.stream.bindings.temperatureAlarm.destination=" + NonTransactionalTemperatureMonitoringTest.TEMPERATURE_ALARM_TOPIC,
        // Must set retries to non-zero when using the idempotent producer
        "spring.cloud.stream.kafka.binder.producerProperties.retries=3",
        "spring.cloud.stream.kafka.binder.consumerProperties.isolation.level=read_committed",
        // Must set acks to all in order to use the idempotent producer. Otherwise we cannot guarantee idempotence.
        "spring.cloud.stream.kafka.binder.producerProperties.acks=all",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration",
        //"spring.cloud.stream.kafka.binder.transaction.transactionIdPrefix=tx-producer"
})

@EmbeddedKafka(
        // The size of the current ISR Set(0) is insufficient to satisfy the min.isr requirement of 2 for partition
        count = 2,
        partitions = 2, // default
        brokerProperties = {
        // for test ->  Number of alive brokers '1' does not meet the required replication factor '3' for the transactions state topic
        // (configured via 'transaction.state.log.replication.factor').
        // This error can be ignored if the cluster is starting up and not all brokers are up yet.
        "transaction.state.log.replication.factor=2"
})

class NonTransactionalTemperatureMonitoringTest {

    public static final String TEMPERATURE_ALARM_TOPIC = "temperature-alarm";
    private TemperatureAlarm temperatureAlarm;
    @Autowired
    private TemperatureMonitoring temperatureMonitoring;
    @Autowired
    private TemperatureAlarmRepository repository;
    @MockBean
    private MeanExceptionThrower exceptionThrower;

    @Test
    public void happyPath() {
        TemperatureMeasurement measurement = new TemperatureMeasurement(11);
        temperatureMonitoring.monitor(measurement);
        Awaitility.await().untilAsserted(() -> assertThat(temperatureAlarm).isNotNull());
        assertThat(repository.findAll()).isNotEmpty();
    }

    @Test
    public void unhappyPath() {
        RuntimeException exception = new RuntimeException("Mehehehe");
        doThrow(exception).when(exceptionThrower).beforePublishMessage();
        TemperatureMeasurement measurement = new TemperatureMeasurement(11);
        assertThatThrownBy(() -> temperatureMonitoring.monitor(measurement)).isSameAs(exception);
        assertThat(repository.findAll()).isEmpty();
        Awaitility.await().untilAsserted(() -> assertThat(temperatureAlarm).isNotNull());
    }

    @KafkaListener(topics = TEMPERATURE_ALARM_TOPIC, groupId = "test")
    public void onHeatAlarm(GenericMessage heatAlarmMessage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        temperatureAlarm = objectMapper.readValue((byte[]) heatAlarmMessage.getPayload(), TemperatureAlarm.class);
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }
}