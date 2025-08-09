package vn.ptit.model.constants;

/**
 * Contains the prefixes for the config property files <p>
 * Refer to these files in the <a href="">vn/ptit/model/config/properties</a> directory for more details
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface BasePropertiesConstants {

    String DATASOURCE_CONFIG = "spring.datasource";
    String HIKARI_CONFIG = "spring.datasource.hikari";
    String HIKARI_DATASOURCE_CONFIG = "spring.datasource.hikari.data-source-properties";

    String I18N_CONFIG = "app.i18n";
    String RESOURCE_CONFIG = "app.resources";
    String INTERCEPTOR_CONFIG = "app.interceptor";
    String SECURITY_CONFIG = "app.security";
    String SOCKET_CONFIG = "app.socket";
    String CIRCUIT_BREAKER_CONFIG = "app.resilience4j.circuit-breaker";
    String CIRCUIT_BREAKER_RETRY_CONFIG = "app.resilience4j.retry";
    String CIRCUIT_BREAKER_TIME_LIMITER_CONFIG = "app.resilience4j.time-limiter";

    String ENABLED_CIRCUIT_BREAKER_CONFIG = "app.resilience4j.circuit-breaker.enabled";
    String ENABLED_DATASOURCE = "app.features.datasource";
    String ENABLED_AUDITING = "app.features.jpa-auditing";
    String ENABLED_REST_TEMPLATE = "app.features.rest-template";
    String ENABLED_RESOURCE = "app.features.resources";
    String AUTHENTICATION_TYPE = "app.security.authentication-type";
}
