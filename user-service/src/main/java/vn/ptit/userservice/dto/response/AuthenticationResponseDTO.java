package vn.ptit.userservice.dto.response;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationResponseDTO {

    private Integer botId;
    private Integer userId;
    private String fullname;
    private String email;
    private String username;
    private String status;
    private String token;
    private Set<String> authorities = new HashSet<>();

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
