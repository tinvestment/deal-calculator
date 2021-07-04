package ru.davydov.deal.calculator.dto.getoperation.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class Trade {
    private Long id;
    private OffsetDateTime date;
    private BigDecimal quantity;
    private BigDecimal price;
}
