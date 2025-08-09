package vn.ptit.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ptit.userservice.domain.Role;
import vn.ptit.userservice.dto.mapping.IRoleDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(
        value = """
            SELECT r.id, r.name, r.code
            FROM role r
            JOIN account_role ar on r.id = ar.role_id
            WHERE ar.account_id = ?1
        """,
        nativeQuery = true
    )
    List<IRoleDTO> findAllByAccountId(Integer accountId);

    @Query(
        value = """
            SELECT r.id, r.name, r.code FROM role r
            WHERE :keyword IS NULL OR (r.code LIKE :keyword OR r.name LIKE :keyword)
        """,
        nativeQuery = true
    )
    Page<IRoleDTO> findAllWithPaging(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT r.id, r.name, r.code FROM role r WHERE r.id IN (?1)", nativeQuery = true)
    List<IRoleDTO> findAllByIds(Iterable<Integer> roleIds);

    @Query(value = "SELECT r.id, r.name, r.code FROM role r WHERE r.id = ?1", nativeQuery = true)
    Optional<IRoleDTO> findIRoleById(Integer roleId);

    boolean existsByCodeOrName(String code, String name);
    boolean existsByCodeOrNameAndIdNot(String code, String name, Integer id);
}
