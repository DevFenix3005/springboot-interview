package com.roberto.interview.controller;

import java.net.URI;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.roberto.interview.dtos.task.TaskRequest;
import com.roberto.interview.dtos.task.TaskResponse;
import com.roberto.interview.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(final TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public ResponseEntity<List<TaskResponse>> getTasks() throws UserPrincipalNotFoundException {
    return ResponseEntity.ok(taskService.findAll());
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTasks(@Valid @RequestBody final TaskRequest newTask)
    throws UserPrincipalNotFoundException {
    final TaskResponse taskResponse = taskService.save(newTask);
    final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(taskResponse.id());
    return ResponseEntity.created(uri).body(taskResponse);
  }

}
