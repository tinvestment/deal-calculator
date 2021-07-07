package ru.davydov.deal.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.davydov.deal.calculator.dto.brokeraccount.Account;
import ru.davydov.deal.calculator.dto.getoperation.request.GetOperationsBody;
import ru.davydov.deal.calculator.service.db.invest.InvestDao;
import ru.davydov.deal.calculator.service.rest.tinkoff.main.MainApi;
import ru.davydov.deal.calculator.service.rest.tinkoff.terminal.TerminalApi;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class Controller {

    private final MainApi mainApi;
    private final TerminalApi terminalApi;
    private final InvestDao investDao;

    @Value("${tinkoff.session-id}")
    private String sessionId;
    @Value("${rest.tinkoff-terminal-api.app-name}")
    private String appName;
    @Value("${rest.tinkoff-terminal-api.app-version}")
    private String appVersion;

    public void extractAllDeals() {
        if (!isSessionAlive()) {
            log.warn("troubles with getting data from qpi");
            return;
        }
        var investTerminal = terminalApi.getBrokerAccounts(appName, appVersion, sessionId);
        log.info(investTerminal.toString());
        investTerminal.getPayload().getAccounts().forEach(this::extractAndSaveAllDeals);
    }

    private void extractAndSaveAllDeals(Account account) {
        var agreementNumber = account.getAgreementNumber();
        log.info("account number {}", agreementNumber);
        var operationsToDate = OffsetDateTime.now();
        OffsetDateTime operationsFromDate;
        do {
            operationsFromDate = operationsToDate.minusDays(10);
            var getOperationsBody = GetOperationsBody.builder()
                    .from(operationsFromDate)
                    .to(operationsToDate)
                    .overnightsDisabled(true)
                    .brokerAccountId(agreementNumber)
                    .build();
            var operations = terminalApi.getOperations("invest_terminal", sessionId, getOperationsBody);
            operations.getPayload().getItems().forEach(investDao::addDeal);
        } while (account.getStartDate().isBefore(operationsFromDate));
    }

    private boolean isSessionAlive() {
        var sessionStatus = mainApi.keepActiveSession(sessionId);
        return "OK".equals(sessionStatus.getResultCode());
    }
}
