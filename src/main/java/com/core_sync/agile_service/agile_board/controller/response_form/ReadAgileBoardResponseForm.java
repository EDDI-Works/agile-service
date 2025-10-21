package com.core_sync.agile_service.agile_board.controller.response_form;

import com.core_sync.agile_service.agile_board.service.response.ReadAgileBoardResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ReadAgileBoardResponseForm {

    private final Long agileBoardId;
    private final Long projectId;
    private final String title;
    private final String writerNickname;
    private final List<Map<String, Object>> kanbanTicketList;
    private final long totalItems;
    private final int totalPages;

    public static ReadAgileBoardResponseForm from(ReadAgileBoardResponse response) {
        return new ReadAgileBoardResponseForm(
                response.getAgileBoardId(),
                response.getProjectId(),
                response.getTitle(),
                response.getWriterNickname(),
                response.getKanbanTicketList(),
                response.getTotalItems(),
                response.getTotalPages()
        );
    }
}
