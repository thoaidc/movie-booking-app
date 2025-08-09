package vn.ptit.userservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.ptit.model.constants.BaseExceptionConstants;
import vn.ptit.userservice.domain.Account;
import vn.ptit.userservice.dto.mapping.IAuthenticationDTO;
import vn.ptit.userservice.repository.AccountRepository;
import vn.ptit.userservice.repository.AuthorityRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository,
                                    AuthorityRepository authorityRepository) {
        this.accountRepository = accountRepository;
        this.authorityRepository = authorityRepository;
        log.debug("Bean 'UserDetailsServiceImpl' is configured for load user credentials info");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Load user by username: {}", username);
        Optional<IAuthenticationDTO> authentication = accountRepository.findAuthenticationByUsername(username);

        if (authentication.isEmpty()) {
            throw new UsernameNotFoundException(BaseExceptionConstants.ACCOUNT_NOT_FOUND);
        }

        Account account = new Account();
        BeanUtils.copyProperties(authentication.get(), account);
        Set<String> userPermissions = authorityRepository.findAllByAccountId(account.getId());

        Collection<SimpleGrantedAuthority> userAuthorities = userPermissions
                .stream()
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return UserDetailsCustom.customBuilder().account(account).authorities(userAuthorities).build();
    }
}
