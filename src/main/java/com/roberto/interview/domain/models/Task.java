package com.roberto.interview.domain.models;

import java.io.Serial;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AttributeOverride(name = "id", column = @Column(name = "task_id", updatable = false, nullable = false))
public class Task extends AbstractAuditingEntity<Long> {

  @Serial
  private static final long serialVersionUID = 6758619493315531659L;

  @Column(nullable = false, length = 90, updatable = false)
  private String title;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Priority priority;

  private boolean completed;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_profile_id", nullable = false)
  private UserProfile userProfile;

}
