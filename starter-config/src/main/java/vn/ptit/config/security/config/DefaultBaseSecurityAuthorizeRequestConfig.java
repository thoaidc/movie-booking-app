package vn.ptit.config.security.config;

import vn.ptit.model.config.properties.SecurityProps;

import java.util.Optional;

@SuppressWarnings("unused")
public class DefaultBaseSecurityAuthorizeRequestConfig implements BaseSecurityAuthorizeRequestConfig {

    private final SecurityProps securityProps;

    public DefaultBaseSecurityAuthorizeRequestConfig(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Override
    public String[] getPublicPatterns() {
        return Optional.ofNullable(securityProps).orElse(new SecurityProps()).getPublicRequestPatterns();
    }
}
