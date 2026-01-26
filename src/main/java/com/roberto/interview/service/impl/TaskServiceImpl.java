package com.roberto.interview.service.impl;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

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

  private static final String TASKS_CACHE = "tasksByUser";

  private final TaskRepository taskRepository;

  private final UserProfileRepository userProfileRepository;

  private final CacheManager cacheManager;

  public TaskServiceImpl(final TaskRepository taskRepository, final UserProfileRepository userProfileRepository,
                         final CacheManager cacheManager) {
    this.taskRepository = taskRepository;
    this.userProfileRepository = userProfileRepository;
    this.cacheManager = cacheManager;
  }

  public TaskResponse save(final TaskRequest taskRequest) throws UserPrincipalNotFoundException {

    final UserProfile currentUserProfile = SecurityUtils.getCurrentUserProfileLogin(userProfileRepository);
    final Task task = Task.builder()
      .title(taskRequest.title())
      .priority(taskRequest.priority())
      .completed(false)
      .userProfile(currentUserProfile)
      .build();
    final Task newTask = taskRepository.save(task);
    final TaskResponse response = mapToResponse(newTask);
    addTaskToCache(currentUserProfile.getUsername(), response);
    return response;
  }

  public List<TaskResponse> findAll() throws UserPrincipalNotFoundException {
    final UserProfile currentUserProfile = SecurityUtils.getCurrentUserProfileLogin(userProfileRepository);
    final String username = currentUserProfile.getUsername();
    final Cache cache = cacheManager.getCache(TASKS_CACHE);
    if (cache != null) {
      final List<TaskResponse> cachedTasks = getCachedTasks(cache, username);
      if (cachedTasks != null) {
        return cachedTasks;
      }
    }
    final List<TaskResponse> taskResponses = taskRepository.findAllByUserProfile(currentUserProfile)
      .stream()
      .map(this::mapToResponse)
      .toList();
    if (cache != null) {
      cache.put(username, List.copyOf(taskResponses));
    }
    return taskResponses;

  }

  @Override
  public void deleteTaskById(final Long id) {
    taskRepository.findById(id).ifPresent(task -> {
      taskRepository.delete(task);
      evictUserCache(task.getUserProfile().getUsername());
    });
  }

  @Override
  public void completeTaskById(final Long id) {
    taskRepository.findById(id).ifPresent(task -> {
      task.setCompleted(true);
      taskRepository.save(task);
      evictUserCache(task.getUserProfile().getUsername());
    });
  }

  private TaskResponse mapToResponse(final Task task) {
    return new TaskResponse(task.getId(), task.getTitle(), task.getPriority().toString(), task.isCompleted(), task.getCreatedDate());
  }

  private void addTaskToCache(final String username, final TaskResponse taskResponse) {
    final Cache cache = cacheManager.getCache(TASKS_CACHE);
    if (cache == null) {
      return;
    }
    final List<TaskResponse> cachedTasks = getCachedTasks(cache, username);
    if (cachedTasks == null) {
      cache.put(username, List.of(taskResponse));
      return;
    }
    final List<TaskResponse> updatedTasks = new ArrayList<>(cachedTasks);
    updatedTasks.add(taskResponse);
    cache.put(username, List.copyOf(updatedTasks));
  }

  private void evictUserCache(final String username) {
    final Cache cache = cacheManager.getCache(TASKS_CACHE);
    if (cache != null) {
      cache.evict(username);
    }
  }

  @SuppressWarnings("unchecked")
  private List<TaskResponse> getCachedTasks(final Cache cache, final String username) {
    final Cache.ValueWrapper wrapper = cache.get(username);
    if (wrapper == null) {
      return null;
    }
    final Object value = wrapper.get();
    if (value instanceof List<?> cachedList) {
      return (List<TaskResponse>) cachedList;
    }
    return null;
  }

}
