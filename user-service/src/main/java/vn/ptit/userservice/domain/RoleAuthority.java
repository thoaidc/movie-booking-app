package vn.ptit.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import vn.ptit.config.entity.AbstractAuditingEntity;

@Entity
@DynamicInsert // Hibernate only insert the nonnull columns to the database instead of insert the entire table
@DynamicUpdate // Hibernate only updates the changed columns to the database instead of updating the entire table
@Table(name = "role_authority")
@SuppressWarnings("unused")
public class RoleAuthority extends AbstractAuditingEntity {

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "authority_id", nullable = false)
    private Integer authorityId;

    public RoleAuthority() {}

    public RoleAuthority(Integer roleId, Integer authorityId) {
        this.roleId = roleId;
        this.authorityId = authorityId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }
}
