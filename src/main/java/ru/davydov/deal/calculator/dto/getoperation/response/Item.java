package ru.davydov.deal.calculator.dto.getoperation.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class Item {

    private Long id;
    private OffsetDateTime date;
    private String operationType;
    private boolean isMarginCall;
    private String issuer;
    private String instrumentType;
    private String ticker;
    private String isin;
    private String shortName;
    private String logoName;
    private BigDecimal payment;
    private String currency;
    private BigDecimal quantity;
    private BigDecimal quantityRest;
    private String status;
    private String accountType;
    private String accountId;
    private String accountName;
    private String description;
    private String color;
    private String textColor;
    private Set<Trade> trades;
    private BigDecimal currencyCbExchangeRate;
    private BigDecimal paymentInRub;
}
