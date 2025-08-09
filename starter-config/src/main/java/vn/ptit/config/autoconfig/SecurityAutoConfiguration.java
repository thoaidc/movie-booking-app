package vn.ptit.config.autoconfig;

import vn.ptit.config.exception.BaseExceptionHandler;
import vn.ptit.config.security.config.BaseSecurityAuthorizeRequestConfig;
import vn.ptit.config.security.config.DefaultBaseSecurityAuthorizeRequestConfig;
import vn.ptit.config.security.handler.DefaultBaseAccessDeniedHandler;
import vn.ptit.config.security.handler.DefaultBaseAuthenticationEntryPoint;
import vn.ptit.model.common.MessageTranslationUtils;
import vn.ptit.model.config.properties.SecurityProps;
import vn.ptit.model.constants.BaseSecurityConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProps.class)
public class SecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
    private final SecurityProps securityProps;

    public SecurityAutoConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler.class)
    public AccessDeniedHandler defaultAccessDeniedHandler(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[ACCESS_DENIED_AUTO_CONFIG] - Use default access denied handler");
        return new DefaultBaseAccessDeniedHandler(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint defaultAuthenticationEntryPoint(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[AUTHENTICATION_ENTRYPOINT_AUTO_CONFIG] - Use default authentication entry point");
        return new DefaultBaseAuthenticationEntryPoint(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(MvcRequestMatcher.Builder.class)
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        log.debug("[MCV_REQUEST_MATCHER_AUTO_CONFIG] - Use default mvc request matcher builder");
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        log.debug("[PASSWORD_ENCODER_AUTO_CONFIG] - Use default password encoder with encrypt factor: 12");
        return new BCryptPasswordEncoder(BaseSecurityConstants.BCRYPT_COST_FACTOR);
    }

    /**
     * Configure a custom AuthenticationProvider to replace the default provider in Spring Security <p>
     * Method `setHideUserNotFoundExceptions` allows {@link UsernameNotFoundException} to be thrown
     * when an account is not found instead of convert to {@link BadCredentialsException} by default <p>
     * After that, the {@link UsernameNotFoundException} will be handle by {@link BaseExceptionHandler}
     */
    @Bean
    @ConditionalOnBean(UserDetailsService.class)
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        log.debug("[DAO_AUTHENTICATION_PROVIDER_AUTO_CONFIG] - Use default authentication provider");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * This is a bean that provides an AuthenticationManager from Spring Security, used to authenticate users
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        log.debug("[AUTHENTICATION_MANAGER_AUTO_CONFIG] - Use default authentication manager");
        return auth.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(BaseSecurityAuthorizeRequestConfig.class)
    public BaseSecurityAuthorizeRequestConfig defaultBaseSecurityAuthorizeRequestConfig() {
        log.debug("[SECURITY_AUTHORIZE_REQUEST_AUTO_CONFIG] - Use default security authorize request matchers");
        return new DefaultBaseSecurityAuthorizeRequestConfig(securityProps);
    }
}
