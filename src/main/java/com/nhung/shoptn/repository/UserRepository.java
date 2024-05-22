package com.nhung.shoptn.repository;

import com.nhung.shoptn.model.Role;
import com.nhung.shoptn.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where (u.name like %?1%) or (u.email like %?1%) or (u.phone like %?1%) or (u.address like %?1%)")
    List<User> findAll(String keyword);

    @Query("select u from User u where ((u.name like %?1%) or (u.email like %?1%) or (u.phone like %?1%) or (u.address like %?1%)) and (u.isEnable in (1,2))")
    Page<User> findAllCustomer(String keyword, Pageable pageable);

    Optional<User> findByEmail(String email);

    User findByRole(Role role);

    User findUserByEmail(String email);

    @Query("SELECT c from User c where c.id = :userID")
    User findOneUser(@Param("userID") Long userID);

    @Transactional
    @Modifying
    @Query("update User u set u.isEnable = 2 where u.id = :userID")
    void deactivated(@Param("userID") Long userID);

    @Transactional
    @Modifying
    @Query("update User u set u.isEnable = 1 where u.id = :userID")
    void activated(@Param("userID") Long userID);

    @Transactional
    @Modifying
    @Query("update User u set u.isEnable = 0 where u.id = :userID")
    void deleted(@Param("userID") Long userID);

    @Query("select distinct u.role from User u")
    List<String> getRolesList();


    @Query("select u from User u where (u.role in (?1) or ?1 is null) and (u.isEnable in ?2) and " +
            "((u.name like %?3%) or (u.email like %?3%) or (u.phone like %?3%) or (u.address like %?3%) or ?3 is null)")
    Page<User> filterUser(Role roleFilter, List<Integer> statusFilter, String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.id = ?1")
    void changePw(Long userID, String newPw);
}
