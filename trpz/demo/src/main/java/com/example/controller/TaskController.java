package com.example.controller;

import com.example.dto.TaskDTO;
import com.example.entity.Project;
import com.example.entity.Task;
import com.example.services.impl.TaskService;
import com.example.util.ErrorResponse;
import com.example.util.validation.ValidationChain;
import com.example.util.validation.factory.ValidationFactory;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.example.util.ErrorUtils.generateFieldErrorMessage;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final ModelMapper modelMapper;
    private final TaskService taskService;
    private final ValidationChain<Task> validation;

    @Autowired
    public TaskController(ModelMapper modelMapper, TaskService taskService, ValidationFactory<Task> validationFactory) {
        this.modelMapper = modelMapper;
        this.taskService = taskService;
        this.validation = validationFactory.createValidationChain();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@PathVariable int id, @RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult){
        Task task = convertToTask(taskDTO);
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }
        taskService.update(id, task);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus>  delete(@PathVariable int id){
        taskService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public List<TaskDTO> getAll(){
        return taskService.findAll().stream().map(this::convertToTaskDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskDTO getOne(@PathVariable int id){
        return convertToTaskDTO(taskService.findOne(id));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgExc(IllegalArgumentException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return  new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgExc(NoSuchElementException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return  new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult){
        Task task = convertToTask(taskDTO);

        validation.validate(task, bindingResult);

        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }
        taskService.save(task);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Task convertToTask(TaskDTO taskDTO){
        Task task = modelMapper.map(taskDTO, Task.class);
        task.setId(0);
        task.setProjectStorage(new Project(taskDTO.getProjectId()));
        return task;
    }

    private TaskDTO convertToTaskDTO(Task task){
        return modelMapper.map(task, TaskDTO.class);
    }
}