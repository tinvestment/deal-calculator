package ru.davydov.deal.calculator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.davydov.deal.calculator.controller.Controller;

@Slf4j
@SpringBootTest
class DealCalculatorApplicationTests {

    @Autowired
    private Controller controller;

    @Test
    void contextLoads() {
        controller.extractAllDeals();
    }

}
