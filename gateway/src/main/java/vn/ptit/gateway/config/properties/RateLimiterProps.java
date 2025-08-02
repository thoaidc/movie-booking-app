package vn.ptit.gateway.config.properties;

import vn.ptit.gateway.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(prefix = PropertiesConstants.RATE_LIMIT_CONFIG)
public class RateLimiterProps {

    private boolean ipValidationEnabled = true;
    private List<String> excludedIps;
    private List<String> excludedIpPrefixes;

    public boolean isIpValidationEnabled() {
        return ipValidationEnabled;
    }

    public void setIpValidationEnabled(boolean ipValidationEnabled) {
        this.ipValidationEnabled = ipValidationEnabled;
    }

    public List<String> getExcludedIps() {
        return excludedIps;
    }

    public void setExcludedIps(List<String> excludedIps) {
        this.excludedIps = excludedIps;
    }

    public List<String> getExcludedIpPrefixes() {
        return excludedIpPrefixes;
    }

    public void setExcludedIpPrefixes(List<String> excludedIpPrefixes) {
        this.excludedIpPrefixes = excludedIpPrefixes;
    }
}
