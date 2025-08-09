package vn.ptit.config.security.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import vn.ptit.model.config.properties.SecurityProps;
import vn.ptit.model.dto.auth.BaseTokenDTO;
import vn.ptit.model.exception.BaseIllegalArgumentException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class BaseJwtProvider {

    private static final Logger log = LoggerFactory.getLogger(BaseJwtProvider.class);
    private static final String ENTITY_NAME = "sds.ec.security.filter.BaseJwtProvider";
    protected final SecretKey secretKey;
    protected final JwtParser jwtParser;
    protected final long TOKEN_VALIDITY;
    protected final long TOKEN_VALIDITY_FOR_REMEMBER;

    public BaseJwtProvider(SecurityProps securityProps) {
        SecurityProps.JwtConfig jwtConfig = Optional.ofNullable(securityProps).orElse(new SecurityProps()).getJwt();

        if (Objects.isNull(jwtConfig)) {
            log.warn("[JWT_CONFIG_NOT_FOUND_ERROR] - JWT config is null! Fallback to default config");
        }

        String base64SecretKey = jwtConfig.getBase64SecretKey();
        TOKEN_VALIDITY = jwtConfig.getValidity();
        TOKEN_VALIDITY_FOR_REMEMBER = jwtConfig.getValidityForRemember();

        if (!StringUtils.hasText(base64SecretKey)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Could not found secret key to sign JWT");
        }

        log.debug("[JWT_SIGNATURE_AUTO_CONFIG] - Using a Base64-encoded JWT secret key");
        byte[] keyBytes = Base64.getUrlDecoder().decode(base64SecretKey);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
        log.debug("[JWT_SIGNATURE_AUTO_CONFIG] - Sign JWT with algorithm: {}", secretKey.getAlgorithm());
    }

    public abstract String generateToken(BaseTokenDTO tokenDTO);
    public abstract Authentication validateToken(String token);
}
