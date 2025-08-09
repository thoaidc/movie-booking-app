package vn.ptit.gateway.security.handler;

import vn.ptit.gateway.common.SecurityUtils;
import vn.ptit.model.common.MessageTranslationUtils;
import vn.ptit.model.constants.BaseExceptionConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEntryPoint.class);
    private final MessageTranslationUtils messageTranslationUtils;

    public AuthenticationEntryPoint(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        log.error("[AUTHENTICATION_ENTRY_POINT_ERROR] - At: {}", exception.getMessage());
        ServerHttpResponse response = exchange.getResponse();
        String errorMessage = messageTranslationUtils.getMessageI18n(BaseExceptionConstants.UNAUTHORIZED);
        String responseBody = SecurityUtils.convertUnAuthorizeError(response, errorMessage);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
