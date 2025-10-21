package com.core_sync.agile_service.project.controller;

import com.core_sync.agile_service.project.controller.request_form.CreateProjectRequestForm;
import com.core_sync.agile_service.project.controller.request_form.LinkGithubRepositoryRequestForm;
import com.core_sync.agile_service.project.controller.request_form.ListProjectRequestForm;
import com.core_sync.agile_service.project.controller.response_form.CreateProjectResponseForm;
import com.core_sync.agile_service.project.controller.response_form.ListProjectResponseForm;
import com.core_sync.agile_service.project.controller.response_form.ReadProjectResponseForm;
import com.core_sync.agile_service.project.service.ProjectService;
import com.core_sync.agile_service.project.service.request.CreateProjectRequest;
import com.core_sync.agile_service.project.service.response.CreateProjectResponse;
import com.core_sync.agile_service.project.service.response.ListProjectResponse;
import com.core_sync.agile_service.project.service.response.ReadProjectResponse;
import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    final private ProjectService projectService;
    final private RedisCacheService redisCacheService;

    @GetMapping("/list")
    public ListProjectResponseForm projectList(
            @RequestHeader("Authorization") String authorizationHeader,
            @ModelAttribute ListProjectRequestForm requestForm
    ) {
        log.info("projectList() -> {}", requestForm);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("인증 토큰: {}", userToken);

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        ListProjectResponse response = projectService.list(requestForm.toListProjectRequest(), accountId);

        return ListProjectResponseForm.from(
                List.of(response),
                response.getTotalItems(),
                response.getTotalPages()
        );
    }

    @PostMapping("/register")
    public CreateProjectResponseForm registerProject (
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateProjectRequestForm createProjectRequestForm) {
        log.info("registerProject() -> {}", createProjectRequestForm);
        log.info("authorizationHeader -> {}", authorizationHeader);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        CreateProjectResponse response = projectService.register(
                createProjectRequestForm.toCreateProjectRequest(accountId)
        );

        return CreateProjectResponseForm.from(response);
    }

    @GetMapping("/read/{id}")
    public ReadProjectResponseForm readProject(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("id") Long projectId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer perPage) {

        log.info("readProject(): {}", projectId);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        ReadProjectResponse response = projectService.read(projectId, page, perPage, accountId);
        return ReadProjectResponseForm.from(response);
    }

    @GetMapping("/team/{teamId}")
    public ListProjectResponseForm getTeamProjects(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("teamId") Long teamId) {

        log.info("getTeamProjects() -> teamId: {}", teamId);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        List<ListProjectResponse.ProjectInfo> projects = projectService.getProjectsByTeamId(teamId, accountId);

        return ListProjectResponseForm.fromTeamProjects(projects);
    }

    @PostMapping("/{projectId}/link-github")
    public ResponseEntity<String> linkGithubRepository(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("projectId") Long projectId,
            @RequestBody LinkGithubRepositoryRequestForm requestForm) {

        log.info("linkGithubRepository() -> projectId: {}, requestForm: {}", projectId, requestForm);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        projectService.linkGithubRepository(
                projectId,
                requestForm.getRepositoryUrl(),
                requestForm.getRepositoryName(),
                requestForm.getOwner()
        );

        return ResponseEntity.ok("GitHub 저장소가 연동되었습니다.");
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("projectId") Long projectId) {

        log.info("deleteProject() -> projectId: {}", projectId);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        projectService.delete(projectId, accountId);

        return ResponseEntity.ok("프로젝트가 삭제되었습니다.");
    }

    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<String> deleteTeamProjects(
            @PathVariable("teamId") Long teamId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        log.info("deleteTeamProjects() -> teamId: {}", teamId);
        
        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("인증 토큰: {}", userToken);
        
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        projectService.deleteByTeamId(teamId, accountId);
        
        return ResponseEntity.ok("팀의 모든 프로젝트가 삭제되었습니다.");
    }
}
