package com.roberto.interview.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roberto.interview.domain.models.Task;
import com.roberto.interview.domain.models.UserProfile;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findAllByUserProfile(final UserProfile userProfile);

}
