package vn.ptit.config.security.config;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import vn.ptit.config.security.filter.BaseAuthenticationFilter;

public class DefaultBaseSecurityFilterChainConfig extends BaseSecurityFilterChainConfig {

    public DefaultBaseSecurityFilterChainConfig(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                                   AccessDeniedHandler accessDeniedHandler,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   BaseAuthenticationFilter baseAuthenticationFilter) {
        super(securityAuthorizeRequestConfig, accessDeniedHandler, authenticationEntryPoint, baseAuthenticationFilter);
    }
}
