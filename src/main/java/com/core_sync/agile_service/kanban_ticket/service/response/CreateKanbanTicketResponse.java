package com.core_sync.agile_service.kanban_ticket.service.response;

import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateKanbanTicketResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final String domain;
    private final String writerNickname;
    private final Integer backlogNumber;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final Long agileBoardId;
    private final AccountProfileResponse accountProfileResponse;

    public static CreateKanbanTicketResponse from(KanbanTicket kanbanTicket, AccountProfileResponse accountProfileResponse) {
        return new CreateKanbanTicketResponse(
                kanbanTicket.getId(),
                kanbanTicket.getTitle(),
                kanbanTicket.getDescription(),
                kanbanTicket.getStatus() != null ? kanbanTicket.getStatus().name() : null,
                kanbanTicket.getPriority() != null ? kanbanTicket.getPriority().name() : null,
                kanbanTicket.getDomain(),
                accountProfileResponse.getNickname(),
                kanbanTicket.getBacklogNumber(),
                kanbanTicket.getCreateDate(),
                kanbanTicket.getUpdateDate(),
                kanbanTicket.getAgileBoard().getId(),
                accountProfileResponse
        );
    }
}
