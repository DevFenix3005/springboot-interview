package com.roberto.interview.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.roberto.interview.models.Task;

public interface TaskService {

    void save(Task t);

    Optional<Task> findById(UUID id);

    List<Task> findAll();
}
