Testing Spring Cloud Stream Binder (Hoxton Release) for sending and receiving Messages 

1. Using embedded Kafka:
 
    [Testing Sender](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureAlarmOutboundAdapterIT.java)
    
    [Testing Receiver](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureMeasurementsInboundAdapterIT.java)

2. Using Test Binder:
     
     [Testing Sender](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureAlarmOutboundAdapterTest.java)
     
     [Testing Receiver](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureMeasurementInboundAdapterTest.java)

3. Kafka Transactions

    [NonTransactionalTemperatureMonitoringTest](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/NonTransactionalTemperatureMonitoringTest.java)
    
    [TransactionalTemperatureMonitoringTest](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TransactionalTemperatureMonitoringTest.java)
