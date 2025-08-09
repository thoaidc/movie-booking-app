package vn.ptit.model.dto.auth;

import org.springframework.security.core.Authentication;

/**
 * User information after successful authentication, used to generate the access token
 * Used in JwtProvider (Self-Defined)
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class BaseTokenDTO {

    private Authentication authentication; // Contains user authorities information
    private String username;
    private Integer userId;
    private Integer botId;
    private Boolean isRememberMe = false;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BaseTokenDTO instance = new BaseTokenDTO();

        public Builder authentication(Authentication authentication) {
            instance.authentication = authentication;
            return this;
        }

        public Builder username(String username) {
            instance.username = username;
            return this;
        }

        public Builder userId(Integer userId) {
            instance.userId = userId;
            return this;
        }

        public Builder botId(Integer botId) {
            instance.botId = botId;
            return this;
        }

        public Builder rememberMe(boolean rememberMe) {
            instance.isRememberMe = rememberMe;
            return this;
        }

        public BaseTokenDTO build() {
            return instance;
        }
    }

    public BaseTokenDTO() {}

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getRememberMe() {
        return isRememberMe;
    }

    public Boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        isRememberMe = rememberMe;
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }
}
