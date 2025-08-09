package vn.ptit.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ptit.userservice.domain.RoleAuthority;

@Repository
@SuppressWarnings("unused")
public interface RoleAuthorityRepository extends JpaRepository<RoleAuthority, Integer> {}
