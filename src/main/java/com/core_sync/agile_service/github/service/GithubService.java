package com.core_sync.agile_service.github.service;

import com.core_sync.agile_service.github.service.response.GithubRepositoryListResponse;

import java.util.List;
import java.util.Map;

public interface GithubService {
    GithubRepositoryListResponse getRepositories(String accessToken);
    List<Map<String, Object>> getCommits(String accessToken, String owner, String repo, int perPage);
    List<Map<String, Object>> getCommits(String accessToken, String owner, String repo, int page, int perPage);
    Map<String, Object> getCommitDetail(String accessToken, String owner, String repo, String sha);
}
