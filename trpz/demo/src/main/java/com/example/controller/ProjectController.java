package com.example.controller;

import com.example.dto.ProjectDTO;
import com.example.entity.Project;
import com.example.services.impl.ProjectService;
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
@RequestMapping("/project")
public class ProjectController {
    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final ValidationChain<Project> validation;

    @Autowired
    public ProjectController(ModelMapper modelMapper, ProjectService projectService, ValidationFactory<Project> validationFactory) {
        this.modelMapper = modelMapper;
        this.projectService = projectService;
        this.validation = validationFactory.createValidationChain();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus>  edit(@PathVariable int id, @RequestBody @Valid ProjectDTO projectDTO, BindingResult bindingResult){
        Project project = convertToProject(projectDTO);
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }
        projectService.update(id, project);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus>  delete(@PathVariable int id){
        projectService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public List<ProjectDTO>  getAll(){
        return projectService.findAll().stream().map(this::convertToProjectDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectDTO getOne(@PathVariable int id){
        return convertToProjectDTO(projectService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid ProjectDTO projectDTO, BindingResult bindingResult){
        Project project = convertToProject(projectDTO);
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }
        projectService.save(project);
        return ResponseEntity.ok(HttpStatus.OK);
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

    private Project convertToProject(ProjectDTO projectDTO){
        return modelMapper.map(projectDTO, Project.class);
    }

    private ProjectDTO convertToProjectDTO(Project project){
        return modelMapper.map(project, ProjectDTO.class);
    }
}