package ru.davydov.deal.calculator.dto.sessionstatus;

import lombok.*;

@Data
public class SessionStatus {
    private String resultCode;
    private SessionStatusPayload payload;
}
