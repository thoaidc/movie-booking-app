package vn.ptit.config.autoconfig;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.ResourceAccessException;

import vn.ptit.config.interceptor.CircuitBreakerRestTemplateInterceptor;
import vn.ptit.model.config.properties.CircuitBreakerProps;
import vn.ptit.model.config.properties.Resilience4jRetryProps;
import vn.ptit.model.config.properties.Resilience4jTimeLimiterProps;
import vn.ptit.model.constants.BasePropertiesConstants;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Auto config for CircuitBreaker + Retry + TimeLimiter to apply to RestTemplate when calling API <p>
 *
 * Trigger conditions:
 * <ul>
 *     <li>Have property {@link BasePropertiesConstants#ENABLED_CIRCUIT_BREAKER_CONFIG} in config file (Ex: application.yml)</li>
 *     <li>Enable binding properties for CircuitBreakerProps, Resilience4jRetryProps, Resilience4jTimeLimiterProps</li>
 * </ul>
 *
 * @author thoaidc
 */
@AutoConfiguration
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_CIRCUIT_BREAKER_CONFIG, havingValue = "true")
@EnableConfigurationProperties({CircuitBreakerProps.class, Resilience4jRetryProps.class, Resilience4jTimeLimiterProps.class})
public class CircuitBreakerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerAutoConfiguration.class);
    private final CircuitBreakerProps circuitBreakerProps;
    private final Resilience4jRetryProps resilience4jRetryProps;
    private final Resilience4jTimeLimiterProps resilience4jTimeLimiterProps;
    private final String DEFAULT_REST_TEMPLATE = "default-rest-client";

    public CircuitBreakerAutoConfiguration(CircuitBreakerProps circuitBreakerProps,
                                           Resilience4jRetryProps resilience4jRetryProps,
                                           Resilience4jTimeLimiterProps resilience4jTimeLimiterProps) {
        this.circuitBreakerProps = circuitBreakerProps;
        this.resilience4jRetryProps = resilience4jRetryProps;
        this.resilience4jTimeLimiterProps = resilience4jTimeLimiterProps;
    }

    /**
     * Default CircuitBreaker for RestTemplate
     *
     * <ul>
     *     <li>Limit error rate, minimum number of requests</li>
     *     <li>Configure timeout when OPEN state</li>
     *     <li>Count slow calls based on threshold</li>
     *     <li>Note: recordExceptions to determine which exception will trigger CB</li>
     * </ul>
     */
    @Bean
    public CircuitBreaker defaultCircuitBreaker() {
        log.debug("[CIRCUIT_BREAKER_AUTO_CONFIG] - Use default circuit breaker");
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(circuitBreakerProps.getFailureRateThreshold())
                .minimumNumberOfCalls(circuitBreakerProps.getMinimumNumberOfCalls())
                .slidingWindowSize(circuitBreakerProps.getSlidingWindowSize())
                .waitDurationInOpenState(Duration.ofMillis(circuitBreakerProps.getWaitDurationInOpenState()))
                .slowCallDurationThreshold(Duration.ofMillis(circuitBreakerProps.getSlowCallDurationThreshold()))
                .slowCallRateThreshold(circuitBreakerProps.getSlowCallRateThreshold())
                .automaticTransitionFromOpenToHalfOpenEnabled(circuitBreakerProps.isAutomaticTransitionFromOpenToHalfOpenEnabled())
                .permittedNumberOfCallsInHalfOpenState(circuitBreakerProps.getPermittedNumberOfCallsInHalfOpenState())
                .recordExceptions(
                    SocketTimeoutException.class,
                    ResourceAccessException.class,
                    TimeoutException.class,
                    RuntimeException.class
                )
                // If the result is Exception then it is considered a failure
                .recordResult(result -> result instanceof Exception)
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of(DEFAULT_REST_TEMPLATE, config);

        // Event listeners to log when CB changes state or is blocked
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                    log.warn(
                        "RestTemplate Circuit Breaker state transition: {} -> {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()
                    )
                )
                .onCallNotPermitted(event ->
                    log.warn("RestTemplate Circuit Breaker: Call not permitted")
                )
                .onFailureRateExceeded(event ->
                    log.warn("RestTemplate Circuit Breaker: Failure rate exceeded: {}%", event.getFailureRate())
                );

        return circuitBreaker;
    }

    /**
     * Default retry for RestTemplate
     * <u>
     *     <li>Limit the number of retries and the time between retries</li>
     *     <li>Specify the types of exceptions to retry</li>
     * </u>
     */
    @Bean
    public Retry defaultRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(resilience4jRetryProps.getRetryMaxAttempts())
                .waitDuration(Duration.ofMillis(resilience4jRetryProps.getRetryWaitDuration()))
                .retryExceptions(
                    SocketTimeoutException.class,
                    ResourceAccessException.class,
                    TimeoutException.class
                )
                .build();

        Retry retry = Retry.of(DEFAULT_REST_TEMPLATE, config);

        retry.getEventPublisher().onRetry(event ->
            log.warn("RestTemplate Retry attempt {}", event.getNumberOfRetryAttempts(), event.getLastThrowable())
        );

        return retry;
    }

    /**
     * Default TimeLimiter for RestTemplate
     * <ul>
     *     <li>Limit the time to execute a request</li>
     *     <li>Can cancel the request when it expires</li>
     * </ul>
     */
    @Bean
    public TimeLimiter defaultTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(resilience4jTimeLimiterProps.getOverallTimeout()))
                .cancelRunningFuture(resilience4jTimeLimiterProps.isCancelRunningFuture()) // Cancel task if timeout
                .build();

        return TimeLimiter.of(DEFAULT_REST_TEMPLATE, config);
    }

    /**
     * Interceptor integrates CB + Retry + TimeLimiter into RestTemplate
     */
    @Bean
    public CircuitBreakerRestTemplateInterceptor circuitBreakerRestTemplateInterceptor(CircuitBreaker circuitBreaker,
                                                                                       Retry retry,
                                                                                       TimeLimiter timeLimiter) {
        return new CircuitBreakerRestTemplateInterceptor(circuitBreaker, retry, timeLimiter);
    }
}
