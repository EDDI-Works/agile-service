package com.core_sync.agile_service.agile_board.service;


import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.agile_board.repository.AgileBoardRepository;
import com.core_sync.agile_service.agile_board.service.request.CreateAgileBoardRequest;
import com.core_sync.agile_service.agile_board.service.response.CreateAgileBoardResponse;
import com.core_sync.agile_service.agile_board.service.response.ReadAgileBoardResponse;
import com.core_sync.agile_service.client.AccountClient;
import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.client.response.AccountResponse;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.repository.KanbanTicketRepository;
import com.core_sync.agile_service.project.entity.Project;
import com.core_sync.agile_service.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgileBoardServiceImpl implements AgileBoardService {

    final private AccountClient accountClient;
    final private AccountProfileClient accountProfileClient;

    final private ProjectRepository projectRepository;
    final private AgileBoardRepository agileBoardRepository;
    final private KanbanTicketRepository kanbanTicketRepository;

    @Override
    public ReadAgileBoardResponse read(Long agileBoardId, Integer page, Integer perPage, Long accountId) {
        Optional<AgileBoard> maybeAgileBoard = agileBoardRepository.findByIdWithWriter(agileBoardId);

        if (maybeAgileBoard.isEmpty()) {
            log.info("정보가 없습니다!");
            return null;
        }

        AgileBoard agileBoard = maybeAgileBoard.get();

        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);
        Pageable pageable = PageRequest.of(page - 1, perPage);
        Page<KanbanTicket> paginatedAgileBoard = kanbanTicketRepository.findAllByAgileBoardId(agileBoardId, pageable);

        return ReadAgileBoardResponse.from(agileBoard, paginatedAgileBoard.getContent(),
                paginatedAgileBoard.getTotalElements(), paginatedAgileBoard.getTotalPages(), accountProfileResponse, accountProfileClient);
    }

    @Override
    public CreateAgileBoardResponse register(CreateAgileBoardRequest createAgileBoardRequest) {
        log.info("애자일 보드 생성 요청 - accountId: {}, projectId: {}, title: {}", 
            createAgileBoardRequest.getAccountId(), 
            createAgileBoardRequest.getProjectId(), 
            createAgileBoardRequest.getTitle());
        
        Long accountId = createAgileBoardRequest.getAccountId();
        Long projectId = createAgileBoardRequest.getProjectId();
        
        // projectId 유효성 검사
        if (projectId == null) {
            log.error("프로젝트 ID가 null입니다.");
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }

        AccountResponse accountResponse = accountClient.AccountFindById(accountId);
        log.info("account: {}", accountResponse.getId());

        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);
        log.info("account profile: {}", accountProfileResponse);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다. projectId: " + projectId));

        log.info("account project: {}", accountProfileResponse);

        AgileBoard savedAgileBoard = agileBoardRepository.save(createAgileBoardRequest.toAgileBoard(accountProfileResponse.getAccountProfileId(), project));
        return CreateAgileBoardResponse.from(savedAgileBoard, accountProfileResponse, project.getId());
    }
}
