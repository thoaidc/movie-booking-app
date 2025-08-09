package vn.ptit.userservice.dto.mapping;

public interface IAuthorityDTO {

    Integer getId();
    String getName();
    String getCode();
    String getDescription();
    Integer getParentId();
    String getParentCode();
}
