package vn.ptit.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ptit.userservice.domain.Account;
import vn.ptit.userservice.dto.mapping.IAuthenticationDTO;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(
        value = """
            SELECT a.id, a.username, a.password, a.email, a.status
            FROM account a WHERE a.username = ?1 AND status <> 'DELETED'
        """,
        nativeQuery = true
    )
    Optional<IAuthenticationDTO> findAuthenticationByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT COUNT(a.id) FROM Account a WHERE (a.username = ?1 OR a.email = ?2) AND a.id <> ?3")
    Long countByUsernameOrEmailAndIdNot(String username, String email, Integer accountId);

    @Modifying
    @Query(value = "UPDATE account SET status = ?2 WHERE id = ?1", nativeQuery = true)
    void updateAccountStatusById(Integer accountId, String status);
}
