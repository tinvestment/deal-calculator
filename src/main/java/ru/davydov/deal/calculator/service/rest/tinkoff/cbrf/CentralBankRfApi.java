package ru.davydov.deal.calculator.service.rest.tinkoff.cbrf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.cbr.Valuta;
import ru.davydov.deal.calculator.service.rest.tinkoff.terminal.TerminalApi;

@FeignClient(name = "central-bank-rf-api", url = "${rest.central-bank-rf-api.url}", configuration = TerminalApi.Configuration.class)
public interface CentralBankRfApi {

    @GetMapping(path = "XML_valFull.asp")
    Valuta currencyCodesHandbook();
}
