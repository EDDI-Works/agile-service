package com.core_sync.agile_service.project.service.request;

import com.core_sync.agile_service.project.entity.Project;
import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class CreateProjectRequest {
    private final String title;
    private final Long accountId;
    private final Long teamId;

    public CreateProjectRequest(String title, Long accountId) {
        this.title = title;
        this.accountId = accountId;
        this.teamId = null;
    }

    public Project toProject(Long accountProfileId) {
        return new Project(title, accountProfileId, teamId);
    }
}
