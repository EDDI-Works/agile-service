package com.core_sync.agile_service.github.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepositoryListResponse {
    private List<RepositoryInfo> repositories;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepositoryInfo {
        private Long id;
        private String name;
        private String fullName;
        private String htmlUrl;
        private String description;
        private Boolean isPrivate;
        private String owner;
    }
}
