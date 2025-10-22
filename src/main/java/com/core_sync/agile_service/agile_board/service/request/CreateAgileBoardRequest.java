package com.core_sync.agile_service.agile_board.service.request;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.project.entity.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateAgileBoardRequest {
    final private Long projectId;
    final private String title;
    final private Long accountId;

    public AgileBoard toAgileBoard(Long accountProfileId, Project project) {
        return new AgileBoard(title, accountProfileId, project);
    }
}
