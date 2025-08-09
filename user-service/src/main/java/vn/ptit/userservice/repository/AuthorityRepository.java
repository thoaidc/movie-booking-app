package vn.ptit.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.ptit.userservice.domain.Authority;
import vn.ptit.userservice.dto.mapping.IAuthorityDTO;

import java.util.List;
import java.util.Set;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    @Query(
        value = """
            SELECT a.code
            FROM authority a
            JOIN role_authority ra on a.id = ra.authority_id
            JOIN account_role ar on ar.role_id = ra.role_id
            WHERE ar.account_id = ?1
        """,
        nativeQuery = true
    )
    Set<String> findAllByAccountId(Integer accountId);

    @Query(
        value = """
            SELECT a.id, a.name, a.code, a.parent_id as parentId, a.parent_code as parentCode
            FROM authority a
            ORDER BY a.code;
        """,
        nativeQuery = true
    )
    List<IAuthorityDTO> findAllByOrderByCodeAsc();

    @Query(
        value = """
            SELECT a.id
            FROM authority a
            JOIN role_authority ra on a.id = ra.authority_id
            WHERE ra.role_id = ?1
        """,
        nativeQuery = true
    )
    Set<Integer> findAllByRoleId(Integer roleId);
}
