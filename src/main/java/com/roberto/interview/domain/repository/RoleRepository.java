package com.roberto.interview.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roberto.interview.domain.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByRoleName(String roleName);

}
