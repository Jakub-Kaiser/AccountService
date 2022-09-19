package com.AccountService.repository;

import com.AccountService.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u where upper(u.email) = upper(?1)")
    UserEntity findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

}
