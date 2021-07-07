package ru.davydov.deal.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.davydov.deal.calculator.service.rest.tinkoff.FeignMarker;

@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackageClasses = FeignMarker.class)
public class DealCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealCalculatorApplication.class, args);
    }
}