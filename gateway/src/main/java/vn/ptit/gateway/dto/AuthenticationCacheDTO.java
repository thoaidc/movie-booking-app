package vn.ptit.gateway.dto;

import java.util.Set;

public class AuthenticationCacheDTO {
    private Integer botId;
    private Integer userId;
    private String username;
    private Set<String> authorities;
    private String token;

    public AuthenticationCacheDTO(Integer botId, Integer userId, String username, Set<String> authorities, String token) {
        this.botId = botId;
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.token = token;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer botId;
        private Integer userId;
        private String username;
        private Set<String> authorities;
        private String token;

        public Builder withBotId(Integer botId) {
            this.botId = botId;
            return this;
        }

        public Builder withUserId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withAuthorities(Set<String> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationCacheDTO build() {
            return new AuthenticationCacheDTO(botId, userId, username, authorities, token);
        }
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}