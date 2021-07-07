package ru.davydov.deal.calculator.service.db.invest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;
import ru.davydov.deal.calculator.dto.getoperation.response.Item;

@Component
@RequiredArgsConstructor
public class InvestDao {

    private final CqlTemplate cqlTemplate;

    public void addDeal(Item item) {
        var cql = "INSERT INTO investment.deal (account_number, date_time, currency, id, instrument_short_name, isin, " +
                "operation_type, payment, quantity, status, ticker) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] args = {item.getAccountId(), item.getDate().toInstant(), item.getCurrency(), item.getId(), item.getShortName(),
                item.getIsin(), item.getOperationType(), item.getPayment(), item.getQuantity(), item.getStatus(), item.getTicker()};
        cqlTemplate.execute(cql, args);
    }
}