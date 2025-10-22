package com.core_sync.agile_service.ticket_comment.controller.request_form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTicketCommentRequestForm {
    private Long ticketId;
    private String content;
}
