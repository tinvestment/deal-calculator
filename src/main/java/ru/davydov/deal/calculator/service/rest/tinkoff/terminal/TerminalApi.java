package ru.davydov.deal.calculator.service.rest.tinkoff.terminal;

import feign.Logger;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.davydov.deal.calculator.dto.brokeraccount.BrokerAccounts;
import ru.davydov.deal.calculator.dto.getoperation.request.GetOperationsBody;
import ru.davydov.deal.calculator.dto.getoperation.response.Operations;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "tinkoff-terminal-api", url = "${rest.tinkoff-terminal-api.url}", configuration = TerminalApi.Configuration.class)
public interface TerminalApi {

    @GetMapping(path = "/user/broker_accounts")
    BrokerAccounts getBrokerAccounts(@RequestParam String appName,
                                     @RequestParam String appVersion,
                                     @RequestParam String sessionId);

    @PostMapping(path = "/user/operations")
    Operations getOperations(@RequestParam String appName,
                             @RequestParam String sessionId,
                             @RequestBody GetOperationsBody getOperationsBody);

    class Configuration {
        @Bean
        public Logger.Level level(@Value("${rest.tinkoff-terminal-api.logger-level:BASIC}") Logger.Level level) {
            return level;
        }

        @Bean
        public Request.Options options(@Value("${rest.tinkoff-terminal-api.connect-timeout:1000}") int connectTimeout,
                                       @Value("${rest.tinkoff-terminal-api.read-timeout:1000}") int readTimeout) {
            return new Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, false);
        }
    }
}
