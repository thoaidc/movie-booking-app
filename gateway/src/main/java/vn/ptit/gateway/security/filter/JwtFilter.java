package vn.ptit.gateway.security.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import vn.ptit.gateway.common.SecurityUtils;
import vn.ptit.gateway.config.properties.PublicEndpointProps;
import vn.ptit.gateway.constants.CommonConstants;
import vn.ptit.model.common.MessageTranslationUtils;
import vn.ptit.model.constants.BaseExceptionConstants;

import java.nio.charset.StandardCharsets;

@Component
public class JwtFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtProvider jwtProvider;
    private final String[] publicPatterns;
    private final MessageTranslationUtils messageTranslationUtils;

    public JwtFilter(JwtProvider jwtProvider,
                     PublicEndpointProps publicEndpointProps,
                     MessageTranslationUtils messageTranslationUtils) {
        this.jwtProvider = jwtProvider;
        this.publicPatterns = publicEndpointProps.getPublicPatterns();
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();
        log.debug("[GATEWAY_JWT_FILTER] - Filtering request: {}: {}", exchange.getRequest().getMethod(), requestUri);

        if (SecurityUtils.checkIfAuthenticationNotRequired(requestUri, publicPatterns)) {
            return chain.filter(exchange);
        }

        String token = StringUtils.trimToNull(SecurityUtils.retrieveTokenFromHeader(exchange.getRequest()));

        return jwtProvider.validateToken(token)
                .flatMap(authentication -> setAuthentication(exchange, chain, authentication))
                .onErrorResume(error -> handleUnauthorized(exchange, error));
    }

    private Mono<Void> setAuthentication(ServerWebExchange exchange, WebFilterChain chain, Authentication auth) {
        exchange.getAttributes().put(CommonConstants.AUTHENTICATION_EXCHANGE_ATTRIBUTE, auth);
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, Throwable e) {
        log.error("[GATEWAY_JWT_VALIDATE_ERROR] - Token validation failed: {}", e.getMessage(), e);
        String errorMessage = messageTranslationUtils.getMessageI18n(BaseExceptionConstants.UNAUTHORIZED);
        ServerHttpResponse response = exchange.getResponse();
        String responseBody = SecurityUtils.convertUnAuthorizeError(response, errorMessage);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
