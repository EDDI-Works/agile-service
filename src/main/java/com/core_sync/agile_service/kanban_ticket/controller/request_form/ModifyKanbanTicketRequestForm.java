package com.core_sync.agile_service.kanban_ticket.controller.request_form;

import com.core_sync.agile_service.kanban_ticket.service.request.ModifyKanbanTicketRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ModifyKanbanTicketRequestForm {
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final String domain;
    private final String linkedCommitSha;
    private final String linkedCommitMessage;
    private final String linkedCommitUrl;

    public ModifyKanbanTicketRequest toModifyKanbanTicketRequest(Long ticketId) {
        return ModifyKanbanTicketRequest.builder()
                .ticketId(ticketId)
                .title(title)
                .description(description)
                .status(status)
                .priority(priority)
                .domain(domain)
                .linkedCommitSha(linkedCommitSha)
                .linkedCommitMessage(linkedCommitMessage)
                .linkedCommitUrl(linkedCommitUrl)
                .build();
    }
}
