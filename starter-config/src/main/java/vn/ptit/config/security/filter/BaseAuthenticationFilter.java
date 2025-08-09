package vn.ptit.config.security.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import vn.ptit.model.common.JsonUtils;
import vn.ptit.model.constants.BaseHttpStatusConstants;
import vn.ptit.model.dto.response.BaseResponseDTO;
import vn.ptit.model.exception.BaseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public abstract class BaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(BaseAuthenticationFilter.class);

    protected abstract boolean shouldAuthenticateRequest(HttpServletRequest request);
    protected abstract void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain);

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (shouldAuthenticateRequest(request)) {
            try {
                authenticate(request, response, filterChain);
            } catch (BaseException e) {
                handleAuthException(response, e);
                return;
            } catch (Exception e) {
                log.error("[AUTHENTICATION_FILTER_ERROR] - Unable to process exception: {}", e.getClass().getName(), e);
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }

    protected void handleAuthException(HttpServletResponse response, BaseException exception) throws IOException {
        log.error("[AUTHENTICATION_FILTER_ERROR] - {}", exception.getClass().getName(), exception);
        response.setStatus(BaseHttpStatusConstants.UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.UNAUTHORIZED)
                .success(Boolean.FALSE)
                .message(exception.getLocalizedMessage())
                .build();

        response.getWriter().write(JsonUtils.toJsonString(responseDTO));
        response.flushBuffer();
    }
}
