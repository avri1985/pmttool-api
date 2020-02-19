package io.avly.pmt.services;

import io.avly.pmt.domain.Backlog;
import io.avly.pmt.domain.Project;
import io.avly.pmt.domain.ProjectTask;
import io.avly.pmt.exceptions.ProjectNotFoundException;
import io.avly.pmt.repository.BacklogRepository;
import io.avly.pmt.repository.ProjectRepository;
import io.avly.pmt.repository.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private  ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
//        try {
            //projectTasks to be added to a spesific project, project != null, Backlog exists
//            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            Backlog backlog = projectService.findByProjectIdentifier(projectIdentifier, username).getBacklog();
            //set the backlog to the projectTask
            projectTask.setBacklog(backlog);

            //ID ProjectIdentifier + Summary of the ProjectTask for project sequence
            Integer backlogSequence = backlog.getPTSequence();

            //Update the Backlog Sequence
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //Initial Priority when priority is null
            if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }

            //Initial status when status is null
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
//        } catch (Exception e) {
//            throw new ProjectNotFoundException("Project with ID: " + projectIdentifier + " is not found");
//        }
    }

    public Iterable<ProjectTask> findBacklogById(String project_iden, String username) {
//        Project project = projectRepository.findByProjectIdentifier(project_iden);
        projectService.findByProjectIdentifier(project_iden, username);

//        if (project == null) {
//            throw new ProjectNotFoundException("Project with ID: " + project_iden + " does not exist");
//        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(project_iden);
    }

    public ProjectTask findProjectTaskBySequence (String project_id, String sequence, String username) {

        //make sure searching on the existing backlog
//        Backlog backlog = backlogRepository.findByProjectIdentifier(project_id);
//        if (backlog == null) {
//            throw new ProjectNotFoundException("Project with ID: " + project_id + " does not exist");
//        }
        projectService.findByProjectIdentifier(project_id, username);

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);

        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task " + sequence + " does not exist");
        }

        //make sure that the backlog/project ID in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(project_id)) {
            throw new ProjectNotFoundException("Project Task: " + sequence + " does not exist in project: " +project_id);
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updateTask, String project_id, String sequence, String username) {
        ProjectTask projectTask = findProjectTaskBySequence(project_id, sequence, username);

        projectTask =  updateTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deleteProjectTaskByProjectSequence (String project_id, String sequence, String username) {
        ProjectTask projectTask = findProjectTaskBySequence(project_id, sequence, username);

//        --The Ugly Code--
//        Backlog backlog = projectTask.getBacklog();
//        List<ProjectTask> pts = backlog.getProjectTasks();
//        pts.remove(projectTask);
//        backlogRepository.save(backlog);

        projectTaskRepository.delete(projectTask);
    }
}
