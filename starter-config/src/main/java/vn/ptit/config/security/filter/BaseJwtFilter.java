package vn.ptit.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import vn.ptit.config.security.config.BaseSecurityAuthorizeRequestConfig;
import vn.ptit.model.common.SecurityUtils;
import vn.ptit.model.constants.BaseSecurityConstants;

public class BaseJwtFilter extends BaseAuthenticationFilter{

    private static final Logger log = LoggerFactory.getLogger(BaseJwtFilter.class);
    private final BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig;
    private final BaseJwtProvider jwtProvider;

    public BaseJwtFilter(BaseSecurityAuthorizeRequestConfig securityAuthorizeRequestConfig,
                         BaseJwtProvider jwtProvider) {
        this.securityAuthorizeRequestConfig = securityAuthorizeRequestConfig;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldAuthenticateRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.info("[JWT_FILTER] - Filtering {}: {}", request.getMethod(), requestURI);
        return SecurityUtils.checkIfAuthenticationRequired(requestURI, securityAuthorizeRequestConfig.getPublicPatterns());
    }

    @Override
    protected void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String token = retrieveToken(request);
        Authentication authentication = this.jwtProvider.validateToken(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String retrieveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(BaseSecurityConstants.HEADER.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(bearerToken))
            bearerToken = request.getHeader(BaseSecurityConstants.HEADER.AUTHORIZATION_GATEWAY_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BaseSecurityConstants.HEADER.TOKEN_TYPE))
            return bearerToken.substring(7);

        return bearerToken;
    }
}
