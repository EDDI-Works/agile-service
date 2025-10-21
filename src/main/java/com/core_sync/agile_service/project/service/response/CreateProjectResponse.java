package com.core_sync.agile_service.project.service.response;

import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.project.entity.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateProjectResponse {
    private final Long id;
    private final String title;
    private final String writerNickname;
    private final LocalDateTime createDate;
    private final Long teamId;
    private final AccountProfileResponse accountProfileResponse;

    public static CreateProjectResponse from(Project project, Long teamId, AccountProfileResponse accountProfileResponse) {
        return new CreateProjectResponse(
                project.getId(),
                project.getTitle(),
                accountProfileResponse.getNickname(),
                project.getCreateDate(),
                teamId,
                accountProfileResponse
        );
    }

    public Long getProjectId() {
        return id;
    }
}
