package ru.davydov.deal.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.davydov.deal.calculator.controller.Controller;
import ru.davydov.deal.calculator.service.FeignMarker;

@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackageClasses = FeignMarker.class)
public class DealCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealCalculatorApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        var controller = ctx.getBean(Controller.class);
        controller.calculateFinResult();
        return args -> {
        };
    }
}
