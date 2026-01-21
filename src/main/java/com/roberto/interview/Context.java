package com.roberto.interview;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.roberto.interview.dtos.task.TaskResponse;

public class Context {

  private static Context instance;

  private final Map<String, List<TaskResponse>> cache = new ConcurrentHashMap<>();

  private Context() {
  }

  public static synchronized Context getInstance() {
    if (instance == null) {
      instance = new Context();
    }
    return instance;
  }

  public boolean areTheUserTasksStoredInTheCache(final String username) {
    return cache.containsKey(username);
  }

  public List<TaskResponse> getTasksByUsernameInCache(final String username) {
    if (!cache.containsKey(username)) {
      return Collections.emptyList();
    }
    return cache.get(username);
  }

  public void addTaskListToCache(final String username, final List<TaskResponse> tasks) {
    cache.put(username, List.copyOf(tasks));
  }

  public void clearTasksByUsernameInCache(final String username) {
    cache.remove(username);
  }

}
