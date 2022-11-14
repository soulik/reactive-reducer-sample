package com.example.reactor001reducer.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class MetricsLoggingExchangeFilterFunction implements ExchangeFilterFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsLoggingExchangeFilterFunction.class);
    private static final String METRICS_WEBCLIENT_START_TIME = MetricsLoggingExchangeFilterFunction.class.getName() + ".START_TIME";

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request).doOnEach((signal) -> {
            if (!signal.isOnComplete()) {
                Long startTime = signal.getContextView().get(METRICS_WEBCLIENT_START_TIME);
                long duration = System.currentTimeMillis() - startTime;
                LOGGER.info("Downstream called taken {}ms", duration);
            }
        }).contextWrite(ctx -> ctx.put(METRICS_WEBCLIENT_START_TIME, System.currentTimeMillis()));
    }
}