package com.core_sync.agile_service.project.controller.response_form;

import com.core_sync.agile_service.project.service.response.ListProjectResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ListProjectResponseForm {
    private final List<Map<String, Object>> projectList;
    private final Long totalItems;
    private final Integer totalPages;

    public static ListProjectResponseForm from(List<ListProjectResponse> projectListResponses, Long totalItems, Integer totalPages) {
        List<Map<String, Object>> combinedProjectList = projectListResponses.stream()
                .flatMap(response -> response.getProjectListWithNicknames().stream())
                .collect(Collectors.toList());

        return new ListProjectResponseForm(combinedProjectList, totalItems, totalPages);
    }

    public static ListProjectResponseForm fromTeamProjects(List<ListProjectResponse.ProjectInfo> projects) {
        List<Map<String, Object>> projectList = projects.stream()
                .map(project -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", project.getId());
                    map.put("title", project.getTitle());
                    map.put("writerNickname", project.getWriterNickname());
                    map.put("createDate", project.getCreateDate());
                    map.put("updateDate", project.getUpdateDate());
                    return map;
                })
                .collect(Collectors.toList());

        return new ListProjectResponseForm(projectList, (long) projects.size(), 1);
    }
}
