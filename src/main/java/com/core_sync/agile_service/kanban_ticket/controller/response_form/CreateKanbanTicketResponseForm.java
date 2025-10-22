package com.core_sync.agile_service.kanban_ticket.controller.response_form;

import com.core_sync.agile_service.kanban_ticket.service.response.CreateKanbanTicketResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateKanbanTicketResponseForm {
    private final Long id;
    private final String title;
    private final String writerNickname;
    private final Integer backlogNumber;
    private final LocalDateTime createDate;
    private final Long agileBoardId;

    public static CreateKanbanTicketResponseForm from(CreateKanbanTicketResponse response) {
        return new CreateKanbanTicketResponseForm(
                response.getId(),
                response.getTitle(),
                response.getWriterNickname(),
                response.getBacklogNumber(),
                response.getCreateDate(),
                response.getAgileBoardId()
        );
    }
}
