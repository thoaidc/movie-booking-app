package vn.ptit.model.config.properties;

import vn.ptit.model.constants.AuthenticationType;
import vn.ptit.model.constants.BasePropertiesConstants;
import vn.ptit.model.constants.BaseSecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

/**
 * Contains security configurations such as the secret key<p>
 * When the application starts, Spring will automatically create an instance of this class
 * and load the values from configuration files like application.properties or application.yml <p>
 *
 * {@link ConfigurationProperties} helps Spring map config properties to fields,
 * instead of using @{@link Value} for each property individually <p>
 *
 * {@link BasePropertiesConstants#SECURITY_CONFIG} decides the prefix for the configurations that will be mapped <p>
 *
 * See <a href="">application-dev.yml</a> for detail
 *
 * @author thoaidc
 */
@SuppressWarnings({"ConfigurationProperties", "unused"})
@ConfigurationProperties(prefix = BasePropertiesConstants.SECURITY_CONFIG)
public class SecurityProps {

    private String[] publicRequestPatterns;
    private AuthenticationType authenticationType;
    private JwtConfig jwt;

    public String[] getPublicRequestPatterns() {
        return Optional.ofNullable(publicRequestPatterns)
                .orElse(BaseSecurityConstants.REQUEST_MATCHERS.DEFAULT_PUBLIC_API_PATTERNS);
    }

    public void setPublicRequestPatterns(String[] publicRequestPatterns) {
        this.publicRequestPatterns = publicRequestPatterns;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public JwtConfig getJwt() {
        return jwt;
    }

    public void setJwt(JwtConfig jwt) {
        this.jwt = jwt;
    }

    public static class JwtConfig {

        private String base64SecretKey;
        private Long validity;
        private Long validityForRemember;

        public String getBase64SecretKey() {
            return base64SecretKey;
        }

        public void setBase64SecretKey(String base64SecretKey) {
            this.base64SecretKey = base64SecretKey;
        }

        public Long getValidity() {
            return validity;
        }

        public void setValidity(Long validity) {
            this.validity = validity;
        }

        public Long getValidityForRemember() {
            return validityForRemember;
        }

        public void setValidityForRemember(Long validityForRemember) {
            this.validityForRemember = validityForRemember;
        }
    }
}
