package vn.ptit.userservice.service.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.ptit.config.security.filter.BaseJwtProvider;
import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.model.constants.BaseHttpStatusConstants;
import vn.ptit.model.dto.auth.BaseTokenDTO;
import vn.ptit.model.dto.response.BaseResponseDTO;
import vn.ptit.model.exception.BaseAuthenticationException;
import vn.ptit.model.exception.BaseBadRequestException;
import vn.ptit.userservice.constants.ResultConstants;
import vn.ptit.userservice.domain.Account;
import vn.ptit.userservice.dto.request.AuthRequestDTO;
import vn.ptit.userservice.dto.response.AuthenticationResponseDTO;
import vn.ptit.userservice.security.UserDetailsCustom;
import vn.ptit.userservice.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String ENTITY_NAME = "AuthenticationServiceImpl";
    private final AuthenticationManager authenticationManager;
    private final BaseJwtProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager, BaseJwtProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    private Authentication authenticate(String username, String rawPassword) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, rawPassword);

        try {
            return authenticationManager.authenticate(token);
        } catch (UsernameNotFoundException e) {
            log.error("[{}] - Account '{}' does not exists", e.getClass().getSimpleName(), username);
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.ACCOUNT_NOT_FOUND);
        } catch (DisabledException e) {
            log.error("[{}] - Account disabled: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.ACCOUNT_DISABLED);
        } catch (LockedException e) {
            log.error("[{}] - Account locked: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.ACCOUNT_LOCKED);
        } catch (AccountExpiredException e) {
            log.error("[{}] - Account expired: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.ACCOUNT_EXPIRED);
        } catch (CredentialsExpiredException e) {
            log.error("[{}] - Credentials expired {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.CREDENTIALS_EXPIRED);
        } catch (BadCredentialsException e) {
            log.error("[{}] - Bad credentials {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseBadRequestException(ENTITY_NAME, BaseExceptionConstants.BAD_CREDENTIALS);
        } catch (AuthenticationException e) {
            log.error("[{}] Authentication failed: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BaseAuthenticationException(ENTITY_NAME, BaseExceptionConstants.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional
    public BaseResponseDTO authenticate(AuthRequestDTO authRequestDTO) {
        log.debug("Authenticating user: {}", authRequestDTO.getUsername());
        String username = authRequestDTO.getUsername().trim();
        String rawPassword = authRequestDTO.getPassword().trim();

        Authentication authentication = authenticate(username, rawPassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsCustom userDetails = (UserDetailsCustom) authentication.getPrincipal();
        Account account = userDetails.getAccount();
        AuthenticationResponseDTO results = new AuthenticationResponseDTO();
        BeanUtils.copyProperties(account, results);
        results.setAuthorities(userDetails.getSetAuthorities());

        BaseTokenDTO authTokenDTO = BaseTokenDTO.builder()
                .authentication(authentication)
                .userId(account.getId())
                .username(account.getUsername())
                .rememberMe(authRequestDTO.getRememberMe())
                .build();

        String jwtToken = tokenProvider.generateToken(authTokenDTO);
        results.setToken(jwtToken);

        return BaseResponseDTO.builder()
                .code(BaseHttpStatusConstants.ACCEPTED)
                .message(ResultConstants.LOGIN_SUCCESS)
                .success(Boolean.TRUE)
                .result(results)
                .build();
    }
}
