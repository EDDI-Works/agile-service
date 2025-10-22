package com.core_sync.agile_service.kanban_ticket.service.request;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.entity.Priority;
import com.core_sync.agile_service.kanban_ticket.entity.Status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateKanbanTicketRequest {
    final private Long agileBoardId;
    final private String title;
    final private String description;
    final private Status status;
    final private Priority priority;
    final private String domain;
    final private Long accountId;
    final private Long assigneeId;

    public KanbanTicket toKanbanTicket(Long accountProfileId, AgileBoard agileBoard) {
        Status ticketStatus = (status != null) ? status : Status.BACKLOG;
        Priority ticketPriority = (priority != null) ? priority : Priority.MEDIUM;
        KanbanTicket ticket = new KanbanTicket(title, description, ticketStatus, ticketPriority, domain, accountProfileId, agileBoard);
        ticket.setAssigneeId(assigneeId != null ? assigneeId : accountProfileId);
        return ticket;
    }
}
