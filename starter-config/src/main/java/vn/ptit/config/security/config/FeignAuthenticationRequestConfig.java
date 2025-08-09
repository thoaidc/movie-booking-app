package vn.ptit.config.security.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.ptit.model.common.SecurityUtils;
import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.model.constants.BaseSecurityConstants;
import vn.ptit.model.dto.auth.BaseUserDTO;
import vn.ptit.model.exception.BaseAuthenticationException;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class FeignAuthenticationRequestConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignAuthenticationRequestConfig.class);
    private static final String ENTITY_NAME = "sds.ec.feign.FeignAuthenticationRequestConfig";
    private final BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig;

    public FeignAuthenticationRequestConfig(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig) {
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
    }

    @Bean
    public RequestInterceptor securityHeaderInterceptor() {
        return requestTemplate -> {
            String targetUrl = requestTemplate.feignTarget().url();
            String requestUrl = URI.create(targetUrl).getPath() + requestTemplate.path();
            log.info("[FEIGN_REQUEST_FORWARDED] - Filtering: {}", requestUrl);

            if (SecurityUtils.checkIfAuthenticationRequired(requestUrl, securityAuthorizeRequestConfig.getPublicPatterns())) {
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    BaseUserDTO userDTO = (BaseUserDTO) authentication.getPrincipal();
                    Integer botId = userDTO.getBotId();
                    Integer userId = userDTO.getId();
                    String username = userDTO.getUsername();
                    Set<String> authorities = Optional.ofNullable(userDTO.getAuthorities())
                            .orElse(Collections.emptySet())
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    log.info("[FEIGN_REQUEST_FORWARDED] - botId: {}, userId: {}, username: {}", botId, userId, username);
                    requestTemplate.header(BaseSecurityConstants.HEADER.BOT_ID, String.valueOf(botId));
                    requestTemplate.header(BaseSecurityConstants.HEADER.USER_ID, String.valueOf(userId));
                    requestTemplate.header(BaseSecurityConstants.HEADER.USER_NAME, username);
                    requestTemplate.header(BaseSecurityConstants.HEADER.USER_AUTHORITIES, String.join(",", authorities));
                } catch (Exception e) {
                    log.error("[FEIGN_REQUEST_FORWARDED_ERROR] - Missing or invalid authentication: {}", e.getMessage());
                    throw BaseAuthenticationException.builder()
                            .entityName(ENTITY_NAME)
                            .errorKey(BaseExceptionConstants.UNAUTHORIZED)
                            .build();
                }
            }
        };
    }
}
