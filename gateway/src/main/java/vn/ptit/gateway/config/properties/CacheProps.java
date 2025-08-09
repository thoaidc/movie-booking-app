package vn.ptit.gateway.config.properties;

import vn.ptit.gateway.constants.CommonConstants;
import vn.ptit.gateway.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = PropertiesConstants.CACHE_CONFIG)
public class CacheProps {

    private boolean enabled;
    private int ttlMinutes = CommonConstants.Cache.DEFAULT_TIME_TO_LIVE;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTtlMinutes() {
        return ttlMinutes;
    }

    public void setTtlMinutes(int ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }
}
