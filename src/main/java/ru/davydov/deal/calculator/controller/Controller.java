package ru.davydov.deal.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.davydov.deal.calculator.dto.getoperation.request.GetOperationsBody;
import ru.davydov.deal.calculator.dto.getoperation.response.Item;
import ru.davydov.deal.calculator.service.tinkoff.main.MainApi;
import ru.davydov.deal.calculator.service.tinkoff.terminal.TerminalApi;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class Controller {

    private final MainApi mainApi;
    private final TerminalApi terminalApi;

    @Value("${tinkoff.session-id}")
    private String sessionId;
    @Value("${rest.tinkoff-terminal-api.app-name}")
    private String appName;
    @Value("${rest.tinkoff-terminal-api.app-version}")
    private String appVersion;

    public void calculateFinResult() {
        if (!isSessionAlive()) {
            log.warn("troubles with getting data from qpi");
            return;
        }
        var investTerminal = terminalApi.getBrokerAccounts(appName, appVersion, sessionId);
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

    private boolean isSessionAlive() {
        var sessionStatus = mainApi.keepActiveSession(sessionId);
        return "OK".equals(sessionStatus.getResultCode());
    }
}
