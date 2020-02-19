package io.avly.pmt.web;

import io.avly.pmt.domain.ProjectTask;
import io.avly.pmt.services.MapValidationErrorService;
import io.avly.pmt.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{project_iden}")
    public ResponseEntity<?> addPTToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result, @PathVariable String project_iden, Principal principal) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);

        if (errorMap != null) return errorMap;

        ProjectTask projectTask1 = projectTaskService.addProjectTask(project_iden, projectTask, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);
    }

    @GetMapping("/{project_iden}")
    public Iterable<ProjectTask> getProjectBacklog (@PathVariable String project_iden, Principal principal) {
        return projectTaskService.findBacklogById(project_iden, principal.getName());
    }

    @GetMapping("/{project_iden}/{sequence}")
    public ResponseEntity<?> getProjectTask(@PathVariable String project_iden, @PathVariable String sequence, Principal principal){
        ProjectTask projectTask = projectTaskService.findProjectTaskBySequence(project_iden, sequence, principal.getName());

        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{project_iden}/{sequence}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                               @PathVariable String project_iden, @PathVariable String sequence, Principal principal) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);

        if (errorMap != null) return errorMap;

        ProjectTask updateTask = projectTaskService.updateByProjectSequence(projectTask, project_iden, sequence, principal.getName());

        return new ResponseEntity<ProjectTask>(updateTask, HttpStatus.OK);
    }

    @DeleteMapping("/{project_iden}/{sequence}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String project_iden, @PathVariable String sequence, Principal principal) {
        projectTaskService.deleteProjectTaskByProjectSequence(project_iden.toUpperCase(), sequence.toUpperCase(), principal.getName());

        return new ResponseEntity<String>("Project Task " +project_iden+ " was deleted successfully", HttpStatus.OK);
    }

}
