package com.core_sync.agile_service.agile_board.controller.request_form;

import com.core_sync.agile_service.agile_board.service.request.CreateAgileBoardRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateAgileBoardRequestForm {
    final private Long projectId;
    final private String title;

    public CreateAgileBoardRequest toCreateAgileBoardRequest(Long accountId) {
        return new CreateAgileBoardRequest(projectId, title, accountId);
    }
}
