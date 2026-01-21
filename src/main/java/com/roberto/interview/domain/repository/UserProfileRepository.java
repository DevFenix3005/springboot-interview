package com.roberto.interview.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.roberto.interview.domain.models.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  @Query("SELECT u FROM UserProfile u WHERE u.username = :username")
  UserProfile findByUsername(String username);

  @Query("SELECT u FROM UserProfile u LEFT JOIN FETCH u.roles r WHERE u.username = :username and r.roleName = 'ROLE_USER'")
  Optional<UserProfile> findByUsernameAndRolesIsContaining(String username);

}
