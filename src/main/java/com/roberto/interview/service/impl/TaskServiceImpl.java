package com.roberto.interview.service.impl;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.roberto.interview.Context;
import com.roberto.interview.domain.models.Task;
import com.roberto.interview.domain.models.UserProfile;
import com.roberto.interview.domain.repository.TaskRepository;
import com.roberto.interview.domain.repository.UserProfileRepository;
import com.roberto.interview.dtos.task.TaskRequest;
import com.roberto.interview.dtos.task.TaskResponse;
import com.roberto.interview.security.SecurityUtils;
import com.roberto.interview.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;

  private final UserProfileRepository userProfileRepository;

  public TaskServiceImpl(final TaskRepository taskRepository, final UserProfileRepository userProfileRepository) {
    this.taskRepository = taskRepository;
    this.userProfileRepository = userProfileRepository;
  }

  public TaskResponse save(final TaskRequest taskRequest) throws UserPrincipalNotFoundException {

    final UserProfile currentUserProfile = SecurityUtils.getCurrentUserProfileLogin(userProfileRepository);
    final Context context = Context.getInstance();
    context.clearTasksByUsernameInCache(currentUserProfile.getUsername());
    final Task task = Task.builder()
      .title(taskRequest.title())
      .priority(taskRequest.priority())
      .completed(false)
      .userProfile(currentUserProfile)
      .build();
    final Task newTask = taskRepository.save(task);
    return mapToResponse(newTask);
  }

  public List<TaskResponse> findAll() throws UserPrincipalNotFoundException {
    final UserProfile currentUserProfile = SecurityUtils.getCurrentUserProfileLogin(userProfileRepository);
    final Context context = Context.getInstance();
    final String username = currentUserProfile.getUsername();
    if (!context.areTheUserTasksStoredInTheCache(username)) {
      final List<TaskResponse> taskResponses = taskRepository.findAllByUserProfile(currentUserProfile)
        .stream()
        .map(this::mapToResponse)
        .toList();
      context.addTaskListToCache(username, taskResponses);
    }
    return context.getTasksByUsernameInCache(username);

  }

  private TaskResponse mapToResponse(final Task task) {
    return new TaskResponse(task.getId(), task.getTitle(), task.getPriority().toString(), task.isCompleted(), task.getCreatedDate());
  }

}
