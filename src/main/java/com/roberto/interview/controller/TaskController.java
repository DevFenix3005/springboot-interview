package com.roberto.interview.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roberto.interview.models.Task;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @GetMapping
    public List<Task> getTasks() {
        //TODO: implement this method to return the list of tasks
        return List.of();
    }

    @PostMapping
    public Task createTasks() {
        //TODO: implement this method to create a new task
        return null;
    }

}
