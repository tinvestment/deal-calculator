package ru.davydov.deal.calculator.dto.getoperation.request;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class GetOperationsBody {
    private final OffsetDateTime from;
    private final OffsetDateTime to;
    private final boolean overnightsDisabled;
    private final String brokerAccountId;
}
