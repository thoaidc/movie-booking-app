package vn.ptit.config.interceptor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import vn.ptit.model.exception.BaseInternalServerException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * HTTP Request Interceptor automatically applies Circuit Breaker, Retry and TimeLimiter to every HTTP request from RestTemplate
 * @author thoaidc
 */
public class CircuitBreakerRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerRestTemplateInterceptor .class);
    private static final String ENTITY_NAME = "sds.ec.interceptor.CircuitBreakerRestTemplateInterceptor";
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final TimeLimiter timeLimiter;

    public CircuitBreakerRestTemplateInterceptor(CircuitBreaker circuitBreaker, Retry retry, TimeLimiter timeLimiter) {
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
        this.timeLimiter = timeLimiter;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request,
                                        @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) {
        log.info("[CIRCUIT_BREAKER_INTERCEPTOR] - Intercepted request: {} {}", request.getMethod(), request.getURI());
        Supplier<ClientHttpResponse> cbSupplier = getClientHttpResponseSupplier(request, body, execution);
        log.debug("[CIRCUIT_BREAKER] - Supplier wrapped with CircuitBreaker instance '{}'", circuitBreaker.getName());
        Supplier<ClientHttpResponse> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        log.debug("[CIRCUIT_BREAKER_RETRY] - Supplier wrapped with Retry instance '{}'", retry.getName());

        Callable<ClientHttpResponse> decorated = TimeLimiter.decorateFutureSupplier(
            timeLimiter,
            () -> {
                log.debug(
                    "[CIRCUIT_BREAKER_TIME_LIMITER] - Starting async execution with timeout {}ms",
                    timeLimiter.getTimeLimiterConfig().getTimeoutDuration().toMillis()
                );
                return CompletableFuture.supplyAsync(retrySupplier);
            }
        );

        log.debug("[CIRCUIT_BREAKER_TIME_LIMITER] - Callable decorated with TimeLimiter: '{}'", timeLimiter.getName());

        try {
            ClientHttpResponse result = decorated.call();
            log.debug("[CIRCUIT_BREAKER_INTERCEPTOR] - HTTP call completed successfully");
            return result;
        } catch (Exception e) {
            log.error("[CIRCUIT_BREAKER_INTERCEPTOR] - Request failed: {}", e.getMessage(), e);
            throw BaseInternalServerException.builder()
                    .entityName(ENTITY_NAME)
                    .originalMessage(e.getMessage())
                    .error(e.getCause())
                    .build();
        }
    }

    private Supplier<ClientHttpResponse> getClientHttpResponseSupplier(HttpRequest request,
                                                                       byte[] body,
                                                                       ClientHttpRequestExecution execution) {
        Supplier<ClientHttpResponse> supplier = () -> {
            log.debug("[CB_REST_TEMPLATE] - Executing actual HTTP call to {}", request.getURI());

            try {
                ClientHttpResponse response = execution.execute(request, body);
                log.debug("[CB_REST_TEMPLATE] - Received response: {} {}", response.getStatusCode(), response.getStatusText());
                return response;
            } catch (IOException e) {
                log.error("[CB_REST_TEMPLATE] - IOException occurred: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };

        return CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
    }
}
