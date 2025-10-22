package com.core_sync.agile_service.project.controller.response_form;

import com.core_sync.agile_service.project.service.response.ReadProjectResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ReadProjectResponseForm {

    private final Long projectId;
    private final String title;
    private final String writerNickname;
    private final List<Map<String, Object>> agileBoardList;
    private final long totalItems;
    private final int totalPages;
    private final String githubOwner;
    private final String githubRepositoryName;
    private final String githubRepositoryUrl;

    public static ReadProjectResponseForm from(ReadProjectResponse response) {
        return new ReadProjectResponseForm(
                response.getProjectId(),
                response.getTitle(),
                response.getWriterNickname(),
                response.getAgileBoardList(),
                response.getTotalItems(),
                response.getTotalPages(),
                response.getGithubOwner(),
                response.getGithubRepositoryName(),
                response.getGithubRepositoryUrl()
        );
    }
}
