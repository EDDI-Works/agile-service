package com.core_sync.agile_service.project.service;

import com.core_sync.agile_service.project.service.request.CreateProjectRequest;
import com.core_sync.agile_service.project.service.request.ListProjectRequest;
import com.core_sync.agile_service.project.service.response.CreateProjectResponse;
import com.core_sync.agile_service.project.service.response.ListProjectResponse;
import com.core_sync.agile_service.project.service.response.ReadProjectResponse;

import java.util.List;

public interface ProjectService {
    ListProjectResponse list(ListProjectRequest request, Long accountId);
    CreateProjectResponse register(CreateProjectRequest createProjectRequest);
    ReadProjectResponse read(Long projectId, Integer page, Integer perPage, Long accountId);
    List<ListProjectResponse.ProjectInfo> getProjectsByTeamId(Long teamId, Long accountId);
    void linkGithubRepository(Long projectId, String repositoryUrl, String repositoryName, String owner);
    void delete(Long projectId, Long accountId);
    void deleteByTeamId(Long teamId, Long accountId);
}
