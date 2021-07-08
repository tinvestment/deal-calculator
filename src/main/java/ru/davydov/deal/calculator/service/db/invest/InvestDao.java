package ru.davydov.deal.calculator.service.db.invest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;
import ru.cbr.Valuta;
import ru.davydov.deal.calculator.dto.getoperation.response.Item;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestDao {

    private final CqlTemplate cqlTemplate;

    public void addDeal(Item item) {
        var cql = "INSERT INTO investment.deal (account_number, date_time, currency, id, instrument_short_name, isin, " +
                "operation_type, payment, quantity, status, ticker, currency_cb_exchange_rate, payment_in_rub) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        var paymentInRub = item.getPaymentInRub() == null ? item.getPayment() : item.getPaymentInRub();
        Object[] args = {item.getAccountId(), item.getDate().toInstant(), item.getCurrency(), item.getId(), item.getShortName(),
                item.getIsin(), item.getOperationType(), item.getPayment(), item.getQuantity(), item.getStatus(), item.getTicker(),
                item.getCurrencyCbExchangeRate(), paymentInRub};
        cqlTemplate.execute(cql, args);
    }

    public void addCurrencyHandbook(Valuta.Item item) {
        var cql = "INSERT INTO investment.currency_handbook (iso_char_code, id, denomination, eng_name, iso_num_code, parent_code, ru_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Object[] args = {item.getISOCharCode(), item.getID(), item.getNominal(), item.getEngName(), item.getISONumCode(),
                item.getParentCode(), item.getName()};
        log.info("id {} iso_char_code {}", item.getID(), item.getISOCharCode());
        cqlTemplate.execute(cql, args);
    }
}