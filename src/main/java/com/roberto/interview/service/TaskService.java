package com.roberto.interview.service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import com.roberto.interview.dtos.task.TaskRequest;
import com.roberto.interview.dtos.task.TaskResponse;

public interface TaskService {

  TaskResponse save(final TaskRequest taskRequest) throws UserPrincipalNotFoundException;

  List<TaskResponse> findAll() throws UserPrincipalNotFoundException;
}
