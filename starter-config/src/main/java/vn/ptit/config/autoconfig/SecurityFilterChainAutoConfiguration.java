package vn.ptit.config.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import vn.ptit.config.security.config.BaseSecurityAuthorizeRequestConfig;
import vn.ptit.config.security.config.BaseSecurityFilterChainConfig;
import vn.ptit.config.security.config.DefaultBaseSecurityFilterChainConfig;
import vn.ptit.config.security.filter.BaseAuthenticationFilter;
import vn.ptit.config.security.filter.BaseHeaderSecurityFilter;
import vn.ptit.config.security.filter.BaseJwtFilter;
import vn.ptit.config.security.filter.BaseJwtProvider;
import vn.ptit.config.security.filter.DefaultJwtProvider;
import vn.ptit.model.config.properties.SecurityProps;
import vn.ptit.model.constants.BasePropertiesConstants;
import vn.ptit.model.constants.AuthenticationType;

@AutoConfiguration
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
@ConditionalOnBean({
    AccessDeniedHandler.class,
    AuthenticationEntryPoint.class,
    BaseSecurityAuthorizeRequestConfig.class
})
@EnableWebSecurity
public class SecurityFilterChainAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainAutoConfiguration.class);

    @Bean
    public BaseJwtProvider defaultJwtProvider(SecurityProps securityConfig) {
        log.debug("[JWT_PROVIDER_AUTO_CONFIG] - Use default JWT provider");
        return new DefaultJwtProvider(securityConfig);
    }

    @Bean
    @ConditionalOnProperty(
        name = BasePropertiesConstants.AUTHENTICATION_TYPE,
        havingValue = AuthenticationType.JWT_VERIFY_VALUE)
    public BaseAuthenticationFilter defaultJwtFilter(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                                                     BaseJwtProvider jwtProvider) {
        log.debug("[AUTHENTICATION_FILTER_AUTO_CONFIG] - Use `BaseJwtFilter` as default authenticate filter");
        return new BaseJwtFilter(securityAuthorizeRequestConfig, jwtProvider);
    }

    @Bean
    @ConditionalOnProperty(
        name = BasePropertiesConstants.AUTHENTICATION_TYPE,
        havingValue = AuthenticationType.HEADER_FORWARDED_VALUE
    )
    public BaseAuthenticationFilter defaultHeaderSecurityFilter(
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig
    ) {
        log.debug("[AUTHENTICATION_FILTER_AUTO_CONFIG] - Use `BaseHeaderSecurityFilter` as default authenticate filter");
        return new BaseHeaderSecurityFilter(securityAuthorizeRequestConfig);
    }

    @Bean
    @ConditionalOnMissingBean(BaseSecurityFilterChainConfig.class)
    public BaseSecurityFilterChainConfig baseSecurityFilterChainConfig(
        BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
        BaseAuthenticationFilter baseAuthenticationFilter,
        AuthenticationEntryPoint authenticationEntryPoint,
        AccessDeniedHandler accessDeniedHandler
    ) {
        log.debug("[SECURITY_FILTER_CHAIN_AUTO_CONFIG] - Use default security filter chain");
        return new DefaultBaseSecurityFilterChainConfig(
            securityAuthorizeRequestConfig,
            accessDeniedHandler,
            authenticationEntryPoint,
            baseAuthenticationFilter
        );
    }

    @Bean
    public SecurityFilterChain defaulSecurityFilterChain(BaseSecurityFilterChainConfig securityFilterChainConfig,
                                                         MvcRequestMatcher.Builder mvc,
                                                         HttpSecurity http) throws Exception {
        log.debug("[SECURITY_FILTER_CHAIN_AUTO_CONFIG] - Using bean: `defaultSecurityFilterChain`");
        securityFilterChainConfig.cors(http);
        securityFilterChainConfig.addFilters(http);
        securityFilterChainConfig.exceptionHandlers(http);
        securityFilterChainConfig.headersSecurity(http);
        securityFilterChainConfig.sessionManagementStrategy(http);
        securityFilterChainConfig.authorizeHttpRequests(http, mvc);
        return http.build();
    }
}
