package com.core_sync.agile_service.project.service.response;

import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.project.entity.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ListProjectResponse {
    private final List<Project> projectList;
    private final Long totalItems;
    private final Integer totalPages;
    private final AccountProfileResponse accountProfile;

    // 날짜를 포맷하여 반환하는 메소드
    public List<Map<String, Object>> getProjectListWithNicknames() {
        return projectList.stream().map(project -> {
            Map<String, Object> projectMap = new HashMap<>();
            projectMap.put("id", project.getId());
            projectMap.put("title", project.getTitle());
            projectMap.put("nickname", accountProfile.getNickname());
            projectMap.put("createDate", formatDate(project.getCreateDate()));  // 날짜 포맷 적용
            return projectMap;
        }).collect(Collectors.toList());
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    public static ListProjectResponse from(List<Project> projectList, Long totalItems, Integer totalPages, AccountProfileResponse accountProfile) {
        return new ListProjectResponse(projectList, totalItems, totalPages, accountProfile);
    }

    @Getter
    @RequiredArgsConstructor
    public static class ProjectInfo {
        private final Long id;
        private final String title;
        private final String writerNickname;
        private final LocalDateTime createDate;
        private final LocalDateTime updateDate;
    }
}
