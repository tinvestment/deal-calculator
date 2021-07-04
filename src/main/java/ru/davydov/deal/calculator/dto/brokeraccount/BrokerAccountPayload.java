package ru.davydov.deal.calculator.dto.brokeraccount;

import lombok.Data;

import java.util.Set;

@Data
public class BrokerAccountPayload {

    private Set<Account> accounts;
}
