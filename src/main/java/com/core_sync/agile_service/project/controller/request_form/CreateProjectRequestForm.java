package com.core_sync.agile_service.project.controller.request_form;

import com.core_sync.agile_service.project.service.request.CreateProjectRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateProjectRequestForm {
    final private String title;
    final private Long teamId;

    public CreateProjectRequest toCreateProjectRequest(Long accountId) {
        return CreateProjectRequest.builder()
                .title(title)
                .accountId(accountId)
                .teamId(teamId)
                .build();
    }
}
