package com.core_sync.agile_service.ticket_comment.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTicketCommentRequest {
    private Long ticketId;
    private String content;
}
