package com.roberto.interview.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.roberto.interview.domain.models.Task;
import com.roberto.interview.domain.models.UserProfile;

public interface TaskRepository extends JpaRepository<Task, Long> {

  @Query("SELECT t FROM Task t inner join t.userProfile u WHERE u = :userProfile")
  List<Task> findAllByUserProfile(final UserProfile userProfile);

}
