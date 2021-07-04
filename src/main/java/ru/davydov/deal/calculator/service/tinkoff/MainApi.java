package ru.davydov.deal.calculator.service.tinkoff;

import feign.Logger;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.davydov.deal.calculator.dto.sessionstatus.SessionStatus;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "tinkoff-main-api", url = "https://www.tinkoff.ru/api", configuration = MainApi.Configuration.class)
public interface MainApi {

    @GetMapping(path = "common/v1/session_status")
    SessionStatus keepActiveSession(@RequestParam(name = "sessionid") String sessionId);
    
    class Configuration {
        @Bean
        public Logger.Level level(@Value("${rest.tinkoff-main-api.logger-level:FULL}") Logger.Level level) {
            return level;
        }

        @Bean
        public Request.Options options(@Value("${rest.tinkoff-main-api.connect-timeout:1000}") int connectTimeout,
                                       @Value("${rest.tinkoff-main-api.read-timeout:1000}") int readTimeout) {
            return new Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, false);
        }
    }
}
