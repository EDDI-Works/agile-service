package com.core_sync.agile_service.project.service;


import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.agile_board.repository.AgileBoardRepository;
import com.core_sync.agile_service.client.AccountClient;
import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.client.response.AccountResponse;
import com.core_sync.agile_service.kanban_ticket.repository.KanbanTicketRepository;
import com.core_sync.agile_service.project.entity.Project;
import com.core_sync.agile_service.project.repository.ProjectRepository;
import com.core_sync.agile_service.project.service.request.CreateProjectRequest;
import com.core_sync.agile_service.project.service.request.ListProjectRequest;
import com.core_sync.agile_service.project.service.response.CreateProjectResponse;
import com.core_sync.agile_service.project.service.response.ListProjectResponse;
import com.core_sync.agile_service.project.service.response.ReadProjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    final private ProjectRepository projectRepository;
    final private AgileBoardRepository agileBoardRepository;
    final private KanbanTicketRepository kanbanTicketRepository;
    final private AccountClient accountClient;
    final private AccountProfileClient accountProfileClient;

    @Override
    public ListProjectResponse list(ListProjectRequest request, Long accountId) {
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getPerPage());

        Page<Project> boardPage = projectRepository.findAllWithWriter(pageRequest);
        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);

        return ListProjectResponse.from(boardPage.getContent(), boardPage.getTotalElements(), boardPage.getTotalPages(), accountProfileResponse);
    }

    @Override
    @Transactional
    public CreateProjectResponse register(CreateProjectRequest createProjectRequest) {
        log.info("프로젝트 생성 요청 - accountId: {}, title: {}, teamId: {}", 
            createProjectRequest.getAccountId(), createProjectRequest.getTitle(), createProjectRequest.getTeamId());
        
        Long accountId = createProjectRequest.getAccountId();

        AccountResponse accountResponse = accountClient.AccountFindById(accountId);
        log.info("account: {}", accountResponse.getId());

        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);
        log.info("account profile: {}", accountProfileResponse);
        
        // 중복 프로젝트 체크
        Long accountProfileId = accountProfileResponse.getAccountProfileId();
        Optional<Project> existingProject = projectRepository.findByAccountProfileIdAndTitle(
            accountProfileId, createProjectRequest.getTitle());
        
        if (existingProject.isPresent()) {
            log.warn("이미 존재하는 프로젝트 - title: {}, accountProfileId: {}", 
                createProjectRequest.getTitle(), accountProfileId);
            throw new IllegalArgumentException("이미 같은 이름의 프로젝트가 존재합니다.");
        }

        Project newProject = createProjectRequest.toProject(accountProfileId);
        Project savedProject = projectRepository.save(newProject);
        log.info("프로젝트 생성 완료 - projectId: {}", savedProject.getId());
        
        return CreateProjectResponse.from(savedProject, createProjectRequest.getTeamId(), accountProfileResponse);
    }

    @Override
    public ReadProjectResponse read(Long projectId, Integer page, Integer perPage, Long accountId) {
        Optional<Project> maybeProject = projectRepository.findByIdWithWriter(projectId);

        if (maybeProject.isEmpty()) {
            log.info("정보가 없습니다!");
            return null;
        }

        Project project = maybeProject.get();

        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);

        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<AgileBoard> paginatedAgileBoard = agileBoardRepository.findAllByProjectId(projectId, pageable);

        return ReadProjectResponse.from(project, paginatedAgileBoard.getContent(),
                paginatedAgileBoard.getTotalElements(), paginatedAgileBoard.getTotalPages(), accountProfileResponse);
    }

    @Override
    public List<ListProjectResponse.ProjectInfo> getProjectsByTeamId(Long teamId, Long accountId) {
        List<Project> projects = projectRepository.findByTeamIdWithWriter(teamId);
        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);

        return projects.stream()
                .map(project -> new ListProjectResponse.ProjectInfo(
                        project.getId(),
                        project.getTitle(),
                        accountProfileResponse.getNickname(),
                        project.getCreateDate(),
                        project.getUpdateDate()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void linkGithubRepository(Long projectId, String repositoryUrl, String repositoryName, String owner) {
        Optional<Project> maybeProject = projectRepository.findById(projectId);

        if (maybeProject.isEmpty()) {
            throw new IllegalArgumentException("프로젝트를 찾을 수 없습니다.");
        }

        Project project = maybeProject.get();
        
        // GitHub 저장소 정보 설정
        project.setGithubRepositoryUrl(repositoryUrl);
        project.setGithubRepositoryName(repositoryName);
        project.setGithubOwner(owner);
        
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void delete(Long projectId, Long accountId) {
        log.info("프로젝트 삭제 - projectId: {}, accountId: {}", projectId, accountId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        
        // 프로젝트 작성자만 삭제 가능
        if (!project.getAccountProfileId().equals(accountId)) {
            throw new IllegalArgumentException("프로젝트 작성자만 삭제할 수 있습니다.");
        }
        
        // 프로젝트와 연관된 애자일 보드 조회
        List<AgileBoard> agileBoards = agileBoardRepository.findAllByProjectId(projectId);
        
        // 각 애자일 보드의 칸반 티켓 먼저 삭제
        for (AgileBoard board : agileBoards) {
            log.info("애자일 보드의 칸반 티켓 삭제 - boardId: {}", board.getId());
            kanbanTicketRepository.deleteAllByAgileBoardId(board.getId());
        }
        
        // 애자일 보드 삭제
        log.info("애자일 보드 삭제 - 총 {}개", agileBoards.size());
        agileBoardRepository.deleteAll(agileBoards);
        
        // 프로젝트 삭제
        projectRepository.delete(project);
        
        log.info("프로젝트 삭제 완료 - projectId: {}", projectId);
    }
    
    @Override
    @Transactional
    public void deleteByTeamId(Long teamId, Long accountId) {
        log.info("팀 및 회원의 모든 프로젝트 삭제 - teamId: {}, accountId: {}", teamId, accountId);
        
        // AccountProfile 정보 조회
        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);
        Long accountProfileId = accountProfileResponse.getAccountProfileId();
        log.info("accountProfileId: {}", accountProfileId);
        
        // 1. 팀의 모든 프로젝트 조회
        List<Project> teamProjects = projectRepository.findByTeamIdWithWriter(teamId);
        log.info("삭제할 팀 프로젝트 수: {}", teamProjects.size());
        
        // 2. 회원의 개인 프로젝트 조회
        List<Project> userProjects = projectRepository.findByAccountProfileId(accountProfileId);
        log.info("삭제할 회원 개인 프로젝트 수: {}", userProjects.size());
        
        // 3. 모든 프로젝트 삭제 (팀 프로젝트 + 개인 프로젝트)
        deleteProjects(teamProjects, "팀 프로젝트");
        deleteProjects(userProjects, "회원 개인 프로젝트");
        
        log.info("팀 및 회원의 모든 프로젝트 삭제 완료 - teamId: {}, accountId: {}, 총 삭제된 프로젝트 수: {}", 
                teamId, accountId, teamProjects.size() + userProjects.size());
    }
    
    private void deleteProjects(List<Project> projects, String projectType) {
        for (Project project : projects) {
            // 프로젝트와 연관된 애자일 보드 조회
            List<AgileBoard> agileBoards = agileBoardRepository.findAllByProjectId(project.getId());
            
            // 각 애자일 보드의 칸반 티켓 먼저 삭제
            for (AgileBoard board : agileBoards) {
                log.info("{} - 애자일 보드의 칸반 티켓 삭제 - boardId: {}", projectType, board.getId());
                kanbanTicketRepository.deleteAllByAgileBoardId(board.getId());
            }
            
            // 애자일 보드 삭제
            log.info("{} - 애자일 보드 삭제 - 총 {}개", projectType, agileBoards.size());
            agileBoardRepository.deleteAll(agileBoards);
            
            // 프로젝트 삭제
            projectRepository.delete(project);
            log.info("{} - 프로젝트 삭제 완료 - projectId: {}", projectType, project.getId());
        }
    }
}
