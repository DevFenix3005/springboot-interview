package com.roberto.interview.service.impl;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.roberto.interview.domain.models.Task;
import com.roberto.interview.domain.models.UserProfile;
import com.roberto.interview.domain.repository.TaskRepository;
import com.roberto.interview.dtos.task.TaskRequest;
import com.roberto.interview.dtos.task.TaskResponse;
import com.roberto.interview.security.SecurityUtils;
import com.roberto.interview.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;

  private final SecurityUtils securityUtils;

  public TaskServiceImpl(final TaskRepository taskRepository, final SecurityUtils securityUtils) {
    this.taskRepository = taskRepository;
    this.securityUtils = securityUtils;
  }

  @Override
  @CacheEvict(value = "tasks", keyGenerator = "taskListByUserGen")
  public TaskResponse save(final TaskRequest taskRequest) throws UserPrincipalNotFoundException {
    final UserProfile currentUserProfile = securityUtils.getCurrentUserProfileLogin();
    final Task task = Task.builder()
      .title(taskRequest.title())
      .priority(taskRequest.priority())
      .completed(false)
      .userProfile(currentUserProfile)
      .build();
    final Task newTask = taskRepository.save(task);
    return mapToResponse(newTask);
  }

  @Override
  @Cacheable(value = "tasks", keyGenerator = "taskListByUserGen")
  public List<TaskResponse> findAll() throws UserPrincipalNotFoundException {
    final UserProfile currentUserProfile = securityUtils.getCurrentUserProfileLogin();
    return taskRepository.findAllByUserProfile(currentUserProfile)
      .stream()
      .map(this::mapToResponse)
      .toList();

  }

  @Override
  @CacheEvict(value = "tasks", keyGenerator = "taskListByUserGen")
  public void deleteTaskById(final Long id) {
    taskRepository.findById(id).ifPresent(taskRepository::delete);
  }

  @Override
  @CacheEvict(value = "tasks", keyGenerator = "taskListByUserGen")
  public void completeTaskById(final Long id) {
    taskRepository.findById(id).ifPresent(task -> {
      task.setCompleted(true);
      taskRepository.save(task);
    });
  }

  private TaskResponse mapToResponse(final Task task) {
    return new TaskResponse(task.getId(), task.getTitle(), task.getPriority().toString(), task.isCompleted(), task.getCreatedDate());
  }

}
