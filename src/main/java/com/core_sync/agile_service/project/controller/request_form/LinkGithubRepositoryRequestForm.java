package com.core_sync.agile_service.project.controller.request_form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class LinkGithubRepositoryRequestForm {
    private String repositoryUrl;
    private String repositoryName;
    private String owner;
}
