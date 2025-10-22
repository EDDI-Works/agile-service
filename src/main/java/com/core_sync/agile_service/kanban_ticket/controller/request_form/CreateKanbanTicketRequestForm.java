package com.core_sync.agile_service.kanban_ticket.controller.request_form;

import com.core_sync.agile_service.kanban_ticket.entity.Priority;
import com.core_sync.agile_service.kanban_ticket.entity.Status;
import com.core_sync.agile_service.kanban_ticket.service.request.CreateKanbanTicketRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateKanbanTicketRequestForm {
    final private Long agileBoardId;
    final private String title;
    final private String description;
    final private Status status;
    final private Priority priority;
    final private String domain;
    final private Long assigneeId;

    public CreateKanbanTicketRequest toCreateKanbanTicketRequest(Long accountId) {
        return new CreateKanbanTicketRequest(agileBoardId, title, description, status, priority, domain, accountId, assigneeId);
    }
}
