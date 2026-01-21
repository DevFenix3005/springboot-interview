package com.roberto.interview.domain.models;

import java.io.Serial;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles")
@EqualsAndHashCode(callSuper = true, exclude = { "tasks", "roles" })
@AttributeOverride(name = "id", column = @Column(name = "user_profile_id", updatable = false, nullable = false))
public class UserProfile extends AbstractAuditingEntity<Long> {

  @Serial
  private static final long serialVersionUID = -2658843360124323155L;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile")
  private List<Task> tasks;

  @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
  @JoinTable(
    name = "user_role",
    joinColumns = @JoinColumn(name = "user_profile_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();

}
