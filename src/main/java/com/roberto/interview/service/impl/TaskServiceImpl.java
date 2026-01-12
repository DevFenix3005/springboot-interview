package com.roberto.interview.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.roberto.interview.models.Task;
import com.roberto.interview.service.TaskService;

public class TaskServiceImpl implements TaskService {

    private final Map<UUID, Task> store = new HashMap<>();

    public void save(Task t) {
        store.put(t.id(), t);
    }

    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }
}
