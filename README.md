Testing Spring Cloud Stream Binder (Hoxton Release) for sending and receiving Messages 

1. using embedded Kafka:
 
    [Testing Sender](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureAlarmOutboundAdapterIT.java)
    
    [Testing Receiver](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureMeasurementsInboundAdapterIT.java)

2. using Test Binder:
     
     [Testing Sender](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureAlarmOutboundAdapterTest.java)
     
     [Testing Receiver](https://github.com/fabapp/spring-cloud-streams-playground/blob/master/src/test/java/de/fabiankrueger/springcloudstreamsplayground/TemperatureMeasurementInboundAdapterTest.java)

