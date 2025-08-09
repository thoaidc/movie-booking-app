package vn.ptit.userservice.dto.mapping;

public interface IAuthenticationDTO {

    Integer getId();
    String getUsername();
    String getPassword();
    String getEmail();
    String getStatus();
}
