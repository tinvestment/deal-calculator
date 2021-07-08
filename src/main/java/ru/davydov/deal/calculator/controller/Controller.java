package ru.davydov.deal.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.cbr.ValCurs;
import ru.davydov.deal.calculator.dto.brokeraccount.Account;
import ru.davydov.deal.calculator.dto.getoperation.request.GetOperationsBody;
import ru.davydov.deal.calculator.dto.getoperation.response.Item;
import ru.davydov.deal.calculator.service.db.invest.InvestDao;
import ru.davydov.deal.calculator.service.rest.tinkoff.cbrf.CentralBankRfApi;
import ru.davydov.deal.calculator.service.rest.tinkoff.main.MainApi;
import ru.davydov.deal.calculator.service.rest.tinkoff.terminal.TerminalApi;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Controller {

    private final MainApi mainApi;
    private final TerminalApi terminalApi;
    private final InvestDao investDao;
    private final CentralBankRfApi centralBankRfApi;
    private final DateTimeFormatter cbRfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            log.info("from {}, to {}", operationsFromDate, operationsToDate);
            var operations = terminalApi.getOperations("invest_terminal", sessionId, getOperationsBody);
            var items = operations.getPayload().getItems();
            var dealsByDate = items.stream()
                    .collect(Collectors.groupingBy(item -> item.getDate().toLocalDate()));
            dealsByDate.forEach((date, deals) -> {
                var currencies = deals.stream()
                        .map(Item::getCurrency)
                        .collect(Collectors.toSet());
                if (!currencies.isEmpty() && (currencies.size() > 1 || !"RUB".equals(currencies.stream().iterator().next()))) {
                    var stringDate = date.format(cbRfDate);
                    var currencyExchange = centralBankRfApi.currencyExchange(stringDate);
                    var currencyCodeToExchangeRate = currencyExchange.getValute()
                            .stream().collect(Collectors.groupingBy(ValCurs.Valute::getCharCode));
                    deals.stream()
                            .filter(item -> !"RUB".equals(item.getCurrency()))
                            .forEach(item -> {
                                var currency = item.getCurrency();
                                var currencyRate = currencyCodeToExchangeRate.get(currency).get(0);
                                var stringRate = currencyRate.getValue().replace(",", ".");
                                var rate = new BigDecimal(stringRate);
                                item.setPaymentInRub(item.getPayment().multiply(rate));
                                item.setCurrencyCbExchangeRate(rate);
                            });
                }
            });
            items.forEach(investDao::addDeal);
            operationsToDate = operationsFromDate;
        } while (account.getStartDate().isBefore(operationsFromDate));
    }

    public void updateCurrencyHandbook() {
        var currencyInfo = centralBankRfApi.currencyCodesHandbook();
        currencyInfo.getItem().stream()
                .filter(item -> item.getISOCharCode() == null)
                .forEach(investDao::addCurrencyHandbook);
    }

    private boolean isSessionAlive() {
        var sessionStatus = mainApi.keepActiveSession(sessionId);
        return "OK".equals(sessionStatus.getResultCode());
    }
}
