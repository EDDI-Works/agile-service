package com.core_sync.agile_service.kanban_ticket.controller.response_form;

import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.entity.Priority;
import com.core_sync.agile_service.kanban_ticket.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyTicketResponseForm {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String domain;
    private Long accountProfileId;
    private Long assigneeId;
    private Integer backlogNumber;
    private Long agileBoardId;
    private String linkedCommitSha;
    private String linkedCommitMessage;
    private String linkedCommitUrl;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public static MyTicketResponseForm from(KanbanTicket ticket) {
        return new MyTicketResponseForm(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getStatus(),
            ticket.getPriority(),
            ticket.getDomain(),
            ticket.getAccountProfileId(),
            ticket.getAssigneeId(),
            ticket.getBacklogNumber(),
            ticket.getAgileBoard().getId(),
            ticket.getLinkedCommitSha(),
            ticket.getLinkedCommitMessage(),
            ticket.getLinkedCommitUrl(),
            ticket.getCreateDate(),
            ticket.getUpdateDate()
        );
    }
}
