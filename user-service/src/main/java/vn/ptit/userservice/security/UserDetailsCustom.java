package vn.ptit.userservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import vn.ptit.userservice.constants.AccountConstants;
import vn.ptit.userservice.domain.Account;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailsCustom extends User {

    private final Account account;
    private final Set<String> authorities = new HashSet<>();

    private UserDetailsCustom(Account account,
                              Collection<? extends GrantedAuthority> authorities,
                              boolean accountEnabled,
                              boolean accountNonExpired,
                              boolean credentialsNonExpired,
                              boolean accountNonLocked) {
        super(
            account.getUsername(),
            account.getPassword(),
            accountEnabled,
            accountNonExpired,
            credentialsNonExpired,
            accountNonLocked,
            authorities
        );

        this.account = account;
        this.authorities.addAll(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
    }

    public Account getAccount() {
        return account;
    }

    public Set<String> getSetAuthorities() {
        return authorities;
    }

    public static Builder customBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Account account;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean accountEnabled = true;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;

        public Builder account(Account account) {
            this.account = account;

            switch (account.getStatus()) {
                case AccountConstants.STATUS.ACTIVE -> accountEnabled = true;
                case AccountConstants.STATUS.INACTIVE -> accountEnabled = false;
                case AccountConstants.STATUS.LOCKED -> accountNonLocked = false;
                case AccountConstants.STATUS.DELETED -> accountNonExpired = false;
            }

            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public UserDetailsCustom build() {
            boolean credentialsNonExpired = true;

            return new UserDetailsCustom(
                account,
                authorities,
                accountEnabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked
            );
        }
    }
}
