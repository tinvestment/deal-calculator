package ru.davydov.deal.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.davydov.deal.calculator.dto.getoperation.request.GetOperationsBody;
import ru.davydov.deal.calculator.dto.getoperation.response.Item;
import ru.davydov.deal.calculator.service.FeignMarker;
import ru.davydov.deal.calculator.service.tinkoff.MainApi;
import ru.davydov.deal.calculator.service.tinkoff.TerminalApi;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackageClasses = FeignMarker.class)
public class DealCalculatorApplication {

    public static void main(String[] args) {
        var run = SpringApplication.run(DealCalculatorApplication.class, args);
        var mainApi = run.getBeansOfType(MainApi.class).get(MainApi.class.getName());
        var terminalApi = run.getBeansOfType(TerminalApi.class).get(TerminalApi.class.getName());
        var sessionId = "";
        var session = mainApi.keepActiveSession(sessionId);
        log.info(session.toString());
        if (!"OK".equals(session.getResultCode())) {
            return;
        }
        var investTerminal = terminalApi.getBrokerAccounts("invest_terminal", "2.0.0", sessionId);
        log.info(investTerminal.toString());
        investTerminal.getPayload().getAccounts()
                .forEach(account -> {
                    var agreementNumber = account.getAgreementNumber();
                    log.info("account number {}", agreementNumber);
                    var getOperationsBody = GetOperationsBody.builder()
                            .from(OffsetDateTime.now().minusDays(20))
                            .to(OffsetDateTime.now())
                            .overnightsDisabled(true)
                            .brokerAccountId(agreementNumber)
                            .build();
                    var operations = terminalApi.getOperations("invest_terminal", sessionId, getOperationsBody);
                    var operationsByType = operations.getPayload().getItems().stream()
                            .collect(Collectors.groupingBy(Item::getOperationType));
                    log.info(operations.toString());
                    operationsByType.forEach((type, operationsList) -> {
                        log.info(type);
                        var operationsByCurrency = operationsList.stream()
                                .collect(Collectors.groupingBy(Item::getCurrency));
                        operationsByCurrency.forEach((currency, items) -> {
                            var paymentsSum = items.stream()
                                    .map(Item::getPayment)
                                    .reduce(BigDecimal::add);
                            log.info("currency {}, sum {}", currency, paymentsSum);
                        });
                    });
                });
    }

}
