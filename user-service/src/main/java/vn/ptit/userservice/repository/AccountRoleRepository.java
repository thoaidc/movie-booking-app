package vn.ptit.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ptit.userservice.domain.AccountRole;

@Repository
@SuppressWarnings("unused")
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {}
