package vn.ptit.model.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import vn.ptit.model.constants.BasePropertiesConstants;

/**
 * Contains configuration properties related to Circuit Breaker config<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#CIRCUIT_BREAKER_RETRY_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-test.yml</a> for detail
 *
 * @author thoaidc
 */
@SuppressWarnings({"ConfigurationProperties", "unused"})
@ConfigurationProperties(prefix = BasePropertiesConstants.CIRCUIT_BREAKER_RETRY_CONFIG)
public class Resilience4jRetryProps {

    private int retryMaxAttempts;
    private int retryWaitDuration;

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    public int getRetryWaitDuration() {
        return retryWaitDuration;
    }

    public void setRetryWaitDuration(int retryWaitDuration) {
        this.retryWaitDuration = retryWaitDuration;
    }
}
