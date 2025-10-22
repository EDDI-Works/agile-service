package com.core_sync.agile_service.project.service.response;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.project.entity.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ReadProjectResponse {
    final private Long projectId;
    final private String title;
    final private String writerNickname;
    final private List<Map<String, Object>> agileBoardList;
    final private long totalItems;
    final private int totalPages;
    final private AccountProfileResponse accountProfileResponse;
    final private String githubOwner;
    final private String githubRepositoryName;
    final private String githubRepositoryUrl;

    public static ReadProjectResponse from(Project project,
                                           List<AgileBoard> agileBoardList,
                                           long totalItems,
                                           int totalPages,
                                           AccountProfileResponse accountProfileResponse
    ) {

        List<Map<String, Object>> agileBoardMaps = agileBoardList.stream().map(ab -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ab.getId());
            map.put("title", ab.getTitle());
            map.put("writer", accountProfileResponse.getNickname());
            map.put("createDate", ab.getCreateDate());
            return map;
        }).collect(Collectors.toList());

        return new ReadProjectResponse(
                project.getId(),
                project.getTitle(),
                accountProfileResponse.getNickname(),
                agileBoardMaps,
                totalItems,
                totalPages,
                accountProfileResponse,
                project.getGithubOwner(),
                project.getGithubRepositoryName(),
                project.getGithubRepositoryUrl()
        );
    }
}
