package de.fabiankrueger.springcloudstreamsplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringCloudStreamsPlaygroundApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringCloudStreamsPlaygroundApplication.class, args);
  }

}
