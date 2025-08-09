package vn.ptit.gateway.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPatternParser;

import vn.ptit.model.common.JsonUtils;
import vn.ptit.model.constants.BaseHttpStatusConstants;
import vn.ptit.model.constants.BaseSecurityConstants;
import vn.ptit.model.dto.response.BaseResponseDTO;

import java.util.Arrays;

public class SecurityUtils {

    public static String retrieveTokenFromHeader(ServerHttpRequest request) {
        // Extract from Authorization header
        String authHeader = request.getHeaders().getFirst(BaseSecurityConstants.HEADER.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BaseSecurityConstants.HEADER.TOKEN_TYPE)) {
            return authHeader.substring(BaseSecurityConstants.HEADER.TOKEN_TYPE.length());
        }

        // Extract from query parameter (for WebSocket)
        return request.getQueryParams().getFirst("token");
    }

    public static String convertUnAuthorizeError(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.UNAUTHORIZED)
                .success(Boolean.FALSE)
                .message(message)
                .build();

        return JsonUtils.toJsonString(responseDTO);
    }

    public static boolean checkIfAuthenticationNotRequired(String url, String[] publicPatterns) {
        PathPatternParser parser = new PathPatternParser();
        return Arrays.stream(publicPatterns)
                .map(parser::parse)
                .anyMatch(p -> p.matches(PathContainer.parsePath(url)));
    }
}
