package vn.ptit.userservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import vn.ptit.config.entity.AbstractAuditingEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert // Hibernate only insert the nonnull columns to the database instead of insert the entire table
@DynamicUpdate // Hibernate only updates the changed columns to the database instead of updating the entire table
@Table(name = "role")
@SuppressWarnings("unused")
public class Role extends AbstractAuditingEntity {

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 45)
    private String code;

    @ManyToMany(
        cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH },
        fetch = FetchType.LAZY
    )
    @JoinTable(
        name = "role_authority",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id")
    )
    private List<Authority> authorities = new ArrayList<>();

    public Role() {}

    public Role(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
