package com.umbrellaevent.repository;


import com.umbrellaevent.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> getMyUserByUsername(String username);

//    Page<AppUser> findAll(Pageable pageable);

    @Query("select count(*) from AppUser u where u.role.id=:id")
    long countByRole_Id(@Param("id") UUID role_id);



    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByResetToken(String resetToken);
}
