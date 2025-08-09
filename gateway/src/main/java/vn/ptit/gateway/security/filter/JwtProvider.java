package vn.ptit.gateway.security.filter;

import org.springframework.security.core.GrantedAuthority;
import vn.ptit.gateway.common.CacheUtils;
import vn.ptit.gateway.config.properties.CacheProps;
import vn.ptit.gateway.dto.AuthenticationCacheDTO;
import vn.ptit.model.config.properties.SecurityProps;
import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.model.constants.BaseSecurityConstants;
import vn.ptit.model.dto.auth.BaseUserDTO;
import vn.ptit.model.exception.BaseAuthenticationException;
import vn.ptit.model.exception.BaseBadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vn.ptit.model.exception.BaseIllegalArgumentException;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
    private static final String ENTITY_NAME = "sds.ec.security.filter.JwtProvider";
    private final JwtParser jwtParser;
    private final CacheProps cacheConfig;
    private final CacheUtils cacheUtils;

    public JwtProvider(SecurityProps securityProps, CacheProps cacheConfig, CacheUtils cacheUtils) {
        this.cacheConfig = cacheConfig;
        this.cacheUtils = cacheUtils;
        SecurityProps.JwtConfig jwtConfig = Optional.ofNullable(securityProps).orElse(new SecurityProps()).getJwt();

        if (Objects.isNull(jwtConfig)) {
            log.warn("[JWT_CONFIG_NOT_FOUND] - JWT config is null! Fallback to default config");
        }

        String base64SecretKey = jwtConfig.getBase64SecretKey();

        if (!StringUtils.hasText(base64SecretKey)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Could not found secret key to sign JWT");
        }

        log.debug("[GATEWAY_JWT_SIGNATURE_CONFIG] - Using a Base64-encoded JWT secret key");
        byte[] keyBytes = Base64.getUrlDecoder().decode(base64SecretKey);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
        log.debug("[GATEWAY_JWT_SIGNATURE_CONFIG] - Sign JWT with algorithm: {}", secretKey.getAlgorithm());
    }

    public Mono<Authentication> validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Mono.error(new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.BAD_CREDENTIALS));
        }

        return cacheConfig.isEnabled() ? getCachedAuthentication(token) : validateAndCache(token);
    }

    private Mono<Authentication> getCachedAuthentication(String token) {
        return Mono.fromCallable(() -> {
                    AuthenticationCacheDTO authentication = null;

                    try {
                        log.debug("[GET_TOKEN_CACHED] - Get token cached from redis");
                        authentication = cacheUtils.get(token, AuthenticationCacheDTO.class);
                    } catch (Exception e) {
                        log.debug("[TOKEN_NOT_CACHED] - User token not cached from redis: {}", e.getMessage());
                    }

                    if (Objects.isNull(authentication)) {
                        return null;
                    }

                    Set<SimpleGrantedAuthority> userAuthorities = authentication.getAuthorities()
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

                    BaseUserDTO principal = BaseUserDTO.userBuilder()
                            .withBotId(authentication.getBotId())
                            .withId(authentication.getUserId())
                            .withUsername(authentication.getUsername())
                            // Password not used but needed to avoid `argument 'content': null` error in spring security
                            .withPassword(authentication.getUsername())
                            .withAuthorities(userAuthorities)
                            .build();

                    log.debug("[GET_TOKEN_CACHED] - Return authentication cached");
                    return (Authentication) new UsernamePasswordAuthenticationToken(principal, token, userAuthorities);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(validateAndCache(token));
    }

    private Mono<Authentication> validateAndCache(String token) {
        return Mono.fromCallable(() -> parseToken(token))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(authentication -> {
                    BaseUserDTO principal = (BaseUserDTO) authentication.getPrincipal();
                    Set<String> authorities = Optional.ofNullable(principal.getAuthorities())
                            .orElse(Collections.emptySet())
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    AuthenticationCacheDTO authenticationCacheDTO = AuthenticationCacheDTO.builder()
                            .withToken((String) authentication.getCredentials())
                            .withBotId(principal.getBotId())
                            .withUserId(principal.getId())
                            .withUsername(principal.getUsername())
                            .withAuthorities(authorities)
                            .withToken(token)
                            .build();

                    log.debug("[TOKEN_CACHED] - Cached authentication to Redis");
                    cacheUtils.cache(token, authenticationCacheDTO);
                });
    }

    private Authentication parseToken(String token) {
        log.debug("[VALIDATE_TOKEN] - Validating token by default config");

        if (!StringUtils.hasText(token))
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.BAD_CREDENTIALS);

        try {
            return getAuthentication(token);
        } catch (MalformedJwtException e) {
            log.error("[JWT_MALFORMED_ERROR] - Invalid JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("[JWT_SIGNATURE_ERROR] - Invalid JWT signature: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("[JWT_SECURITY_ERROR] - Unable to decode JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("[JWT_EXPIRED_ERROR] - Expired JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("[ILLEGAL_ARGUMENT_ERROR] - Invalid JWT string (null, empty,...): {}", e.getMessage());
        }

        throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.TOKEN_INVALID_OR_EXPIRED);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        log.debug("[RETRIEVE_AUTHENTICATION] - Claim authentication info from token after authenticated");
        Claims claims = (Claims) jwtParser.parse(token).getPayload();
        Integer botId = (Integer) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.BOT_ID);
        Integer userId = (Integer) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USER_ID);
        String username = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.USERNAME);
        String authorities = (String) claims.get(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES);

        Set<SimpleGrantedAuthority> userAuthorities = Arrays.stream(authorities.split(","))
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        BaseUserDTO principal = BaseUserDTO.userBuilder()
                .withBotId(botId)
                .withId(userId)
                .withUsername(username)
                .withPassword(username) // Not used but needed to avoid `argument 'content': null` error in spring security
                .withAuthorities(userAuthorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, token, userAuthorities);
    }
}
