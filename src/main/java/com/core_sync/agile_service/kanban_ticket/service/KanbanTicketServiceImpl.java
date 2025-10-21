package com.core_sync.agile_service.kanban_ticket.service;


import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.agile_board.repository.AgileBoardRepository;
import com.core_sync.agile_service.client.AccountClient;
import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.client.response.AccountResponse;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.entity.Priority;
import com.core_sync.agile_service.kanban_ticket.entity.Status;
import com.core_sync.agile_service.kanban_ticket.repository.KanbanTicketRepository;
import com.core_sync.agile_service.kanban_ticket.service.request.CreateKanbanTicketRequest;
import com.core_sync.agile_service.kanban_ticket.service.request.ModifyKanbanTicketRequest;
import com.core_sync.agile_service.kanban_ticket.service.response.CreateKanbanTicketResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KanbanTicketServiceImpl implements KanbanTicketService {

    final private AccountClient accountClient;
    final private AccountProfileClient accountProfileClient;
    final private AgileBoardRepository agileBoardRepository;
    final private KanbanTicketRepository kanbanTicketRepository;

    @Override
    public CreateKanbanTicketResponse register(CreateKanbanTicketRequest createKanbanTicketRequest) {
        log.info("accountId: {}", createKanbanTicketRequest.getAccountId());
        Long accountId = createKanbanTicketRequest.getAccountId();

        AccountResponse accountResponse = accountClient.AccountFindById(accountId);

        log.info("account: {}", accountResponse.getId());


        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);


        log.info("account profile: {}", accountProfileResponse);

        AgileBoard agileBoard = agileBoardRepository.findById(createKanbanTicketRequest.getAgileBoardId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        log.info("account project: {}", accountProfileResponse);

        // 해당 보드의 현재 티켓 개수를 조회하여 백로그 넘버 할당
        long currentTicketCount = kanbanTicketRepository.countByAgileBoardId(createKanbanTicketRequest.getAgileBoardId());
        int newBacklogNumber = (int) (currentTicketCount + 1);
        
        KanbanTicket kanbanTicket = createKanbanTicketRequest.toKanbanTicket(accountProfileResponse.getAccountProfileId(), agileBoard);
        kanbanTicket.setBacklogNumber(newBacklogNumber);
        
        KanbanTicket savedKanbanTicket = kanbanTicketRepository.save(kanbanTicket);
        return CreateKanbanTicketResponse.from(savedKanbanTicket, accountProfileResponse);
    }

    @Override
    @Transactional
    public CreateKanbanTicketResponse modify(ModifyKanbanTicketRequest modifyKanbanTicketRequest,  Long accountId) {
        log.info("modify ticket: {}", modifyKanbanTicketRequest);

        KanbanTicket kanbanTicket = kanbanTicketRepository.findById(modifyKanbanTicketRequest.getTicketId())
                .orElseThrow(() -> new RuntimeException("Kanban Ticket not found"));

        // 티켓 정보 업데이트
        if (modifyKanbanTicketRequest.getTitle() != null) {
            kanbanTicket.setTitle(modifyKanbanTicketRequest.getTitle());
            log.info("Updated title: {}", modifyKanbanTicketRequest.getTitle());
        }
        if (modifyKanbanTicketRequest.getDescription() != null) {
            kanbanTicket.setDescription(modifyKanbanTicketRequest.getDescription());
            log.info("Updated description: {}", modifyKanbanTicketRequest.getDescription());
        }
        if (modifyKanbanTicketRequest.getStatus() != null) {
            try {
                Status statusEnum = Status.valueOf(modifyKanbanTicketRequest.getStatus());
                kanbanTicket.setStatus(statusEnum);
                log.info("Updated status: {} -> {}", modifyKanbanTicketRequest.getStatus(), statusEnum);
            } catch (IllegalArgumentException e) {
                log.error("Invalid status value: {}", modifyKanbanTicketRequest.getStatus(), e);
            }
        }
        if (modifyKanbanTicketRequest.getPriority() != null) {
            try {
                Priority priorityEnum = Priority.valueOf(modifyKanbanTicketRequest.getPriority());
                kanbanTicket.setPriority(priorityEnum);
                log.info("Updated priority: {} -> {}", modifyKanbanTicketRequest.getPriority(), priorityEnum);
            } catch (IllegalArgumentException e) {
                log.error("Invalid priority value: {}", modifyKanbanTicketRequest.getPriority(), e);
            }
        }
        if (modifyKanbanTicketRequest.getDomain() != null) {
            kanbanTicket.setDomain(modifyKanbanTicketRequest.getDomain());
            log.info("Updated domain: {}", modifyKanbanTicketRequest.getDomain());
        }
        if (modifyKanbanTicketRequest.getAssigneeId() != null) {
            kanbanTicket.setAssigneeId(modifyKanbanTicketRequest.getAssigneeId());
            log.info("Updated assigneeId: {}", modifyKanbanTicketRequest.getAssigneeId());
        }
        
        // GitHub 커밋 연결 정보 업데이트
        if (modifyKanbanTicketRequest.getLinkedCommitSha() != null) {
            kanbanTicket.setLinkedCommitSha(modifyKanbanTicketRequest.getLinkedCommitSha());
            log.info("Updated linkedCommitSha: {}", modifyKanbanTicketRequest.getLinkedCommitSha());
        }
        if (modifyKanbanTicketRequest.getLinkedCommitMessage() != null) {
            kanbanTicket.setLinkedCommitMessage(modifyKanbanTicketRequest.getLinkedCommitMessage());
            log.info("Updated linkedCommitMessage: {}", modifyKanbanTicketRequest.getLinkedCommitMessage());
        }
        if (modifyKanbanTicketRequest.getLinkedCommitUrl() != null) {
            kanbanTicket.setLinkedCommitUrl(modifyKanbanTicketRequest.getLinkedCommitUrl());
            log.info("Updated linkedCommitUrl: {}", modifyKanbanTicketRequest.getLinkedCommitUrl());
        }

        AccountProfileResponse accountProfileResponse = accountProfileClient.AccountProfileFindById(accountId);

        log.info("Saving ticket with status: {}, priority: {}", kanbanTicket.getStatus(), kanbanTicket.getPriority());
        KanbanTicket updatedTicket = kanbanTicketRepository.save(kanbanTicket);
        return CreateKanbanTicketResponse.from(updatedTicket, accountProfileResponse);
    }

    @Override
    @Transactional
    public void delete(Long ticketId) {
        log.info("delete ticket: {}", ticketId);
        
        KanbanTicket kanbanTicket = kanbanTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Kanban Ticket not found"));
        
        // 티켓 삭제 (백로그 넘버는 재정렬하지 않음 - 커밋 메시지와 연결되어 있음)
        kanbanTicketRepository.delete(kanbanTicket);
        log.info("Deleted ticket with backlog number: {}", kanbanTicket.getBacklogNumber());
    }

    @Override
    public List<KanbanTicket> getMyTickets(Long accountProfileId) {
        log.info("getMyTickets for accountProfileId: {}", accountProfileId);
        return kanbanTicketRepository.findAllByAccountProfileIdOrAssigneeId(accountProfileId);
    }

}
