package ru.davydov.deal.calculator.dto.getoperation.response;

import lombok.Data;

import java.util.Set;

@Data
public class OperationPayload {

    private Set<Item> items;
}
