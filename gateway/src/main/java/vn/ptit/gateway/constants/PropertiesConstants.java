package vn.ptit.gateway.constants;

/**
 * Contains the prefixes for the config property files
 *
 * @author thoaidc
 */
public interface PropertiesConstants {
    String RATE_LIMIT_CONFIG = "app.gateway.rate-limiter";
    String CACHE_CONFIG = "app.gateway.cache";
    String ENABLED_CACHE = "app.gateway.cache.enabled";
    String SECURITY_REQUEST_CONFIG = "app.gateway.security.request";
    String CORS_CONFIG = "spring.cloud.gateway.globalcors";
}
