package vn.ptit.config.security.filter;

import vn.ptit.model.config.properties.SecurityProps;
import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.model.constants.BaseSecurityConstants;
import vn.ptit.model.dto.auth.BaseTokenDTO;
import vn.ptit.model.dto.auth.BaseUserDTO;
import vn.ptit.model.exception.BaseAuthenticationException;
import vn.ptit.model.exception.BaseBadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultJwtProvider extends BaseJwtProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultJwtProvider.class);
    private static final String ENTITY_NAME = "sds.ec.security.filter.DefaultJwtProvider";

    public DefaultJwtProvider(SecurityProps securityProps) {
        super(securityProps);
    }

    @Override
    public String generateToken(BaseTokenDTO tokenDTO) {
        long tokenValidityInMilliseconds = Instant.now().toEpochMilli();

        if (tokenDTO.isRememberMe())
            tokenValidityInMilliseconds += this.TOKEN_VALIDITY_FOR_REMEMBER;
        else
            tokenValidityInMilliseconds += this.TOKEN_VALIDITY;

        log.debug("[GENERATE_TOKEN] - Token expired after: {}ms", tokenValidityInMilliseconds);
        return generateToken(tokenDTO, tokenValidityInMilliseconds);
    }

    @Override
    public Authentication validateToken(String token) {
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

    private String generateToken(BaseTokenDTO tokenDTO, long tokenValidity) {
        Authentication authentication = tokenDTO.getAuthentication();
        Set<String> userAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        long validityInMilliseconds = Instant.now().toEpochMilli() + tokenValidity;

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.BOT_ID, tokenDTO.getBotId())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.USER_ID, tokenDTO.getUserId())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.USERNAME, tokenDTO.getUsername())
                .claim(BaseSecurityConstants.TOKEN_PAYLOAD.AUTHORITIES, String.join(",", userAuthorities))
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(validityInMilliseconds))
                .compact();
    }

    private Authentication getAuthentication(String token) {
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
