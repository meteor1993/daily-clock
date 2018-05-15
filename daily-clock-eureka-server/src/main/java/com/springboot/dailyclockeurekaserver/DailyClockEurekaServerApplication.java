package com.springboot.dailyclockeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DailyClockEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyClockEurekaServerApplication.class, args);
    }
}
