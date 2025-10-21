package com.core_sync.agile_service.kanban_ticket.service.request;

import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ModifyKanbanTicketRequest {
    private final Long ticketId;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final String domain;
    private final Long assigneeId;
    private final String linkedCommitSha;
    private final String linkedCommitMessage;
    private final String linkedCommitUrl;
}
