package vn.ptit.gateway.security.handler;

import vn.ptit.model.common.JsonUtils;
import vn.ptit.model.common.MessageTranslationUtils;
import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.model.constants.BaseHttpStatusConstants;
import vn.ptit.model.dto.response.BaseResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AccessDeniedHandler implements ServerAccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(AccessDeniedHandler.class);
    private final MessageTranslationUtils messageTranslationUtils;

    public AccessDeniedHandler(MessageTranslationUtils messageTranslationUtils) {
        this.messageTranslationUtils = messageTranslationUtils;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception) {
        log.error("[ACCESS_DENIED_ERROR] - error: {}", exception.getMessage());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.FORBIDDEN)
                .success(Boolean.FALSE)
                .message(messageTranslationUtils.getMessageI18n(BaseExceptionConstants.FORBIDDEN))
                .build();

        String responseBody = JsonUtils.toJsonString(responseDTO);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
