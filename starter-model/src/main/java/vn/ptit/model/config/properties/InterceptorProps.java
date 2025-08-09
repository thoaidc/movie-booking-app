package vn.ptit.model.config.properties;

import vn.ptit.model.constants.BasePropertiesConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#INTERCEPTOR_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@SuppressWarnings({"ConfigurationProperties", "unused"})
@ConfigurationProperties(prefix = BasePropertiesConstants.INTERCEPTOR_CONFIG)
public class InterceptorProps {

    private String[] excludedPatterns;
    private CorsConfig cors;

    public String[] getExcludedPatterns() {
        return excludedPatterns;
    }

    public void setExcludedPatterns(String[] excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    public CorsConfig getCors() {
        return cors;
    }

    public void setCors(CorsConfig cors) {
        this.cors = cors;
    }

    public static class CorsConfig {
        private List<String> applyFor;
        private List<String> allowedOriginPatterns;
        private List<String> allowedHeaders;
        private List<String> allowedMethods;
        private Boolean allowedCredentials;

        public List<String> getApplyFor() {
            return applyFor;
        }

        public void setApplyFor(List<String> applyFor) {
            this.applyFor = applyFor;
        }

        public List<String> getAllowedOriginPatterns() {
            return allowedOriginPatterns;
        }

        public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
            this.allowedOriginPatterns = allowedOriginPatterns;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public Boolean isAllowedCredentials() {
            return allowedCredentials;
        }

        public void setAllowedCredentials(Boolean allowedCredentials) {
            this.allowedCredentials = allowedCredentials;
        }
    }
}
