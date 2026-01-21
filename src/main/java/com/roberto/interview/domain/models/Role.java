package com.roberto.interview.domain.models;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@EqualsAndHashCode(callSuper = true, exclude = { "users" })
@AttributeOverride(name = "id", column = @Column(name = "role_id", updatable = false, nullable = false))
public class Role extends AbstractAuditingEntity<Long> {

  @Serial
  private static final long serialVersionUID = -6224718841898856359L;

  @NonNull
  @Column(name = "role_name", nullable = false, unique = true)
  private String roleName;

  @ManyToMany(mappedBy = "roles")
  private Set<UserProfile> users = new HashSet<>();

}
