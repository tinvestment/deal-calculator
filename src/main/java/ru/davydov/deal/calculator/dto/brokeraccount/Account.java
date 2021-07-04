package ru.davydov.deal.calculator.dto.brokeraccount;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Account {
    private String agreementNumber;
    private String clientId;
    private String status;
    private OffsetDateTime startDate;
}
