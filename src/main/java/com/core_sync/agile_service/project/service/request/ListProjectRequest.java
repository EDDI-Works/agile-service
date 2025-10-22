package com.core_sync.agile_service.project.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ListProjectRequest {
    final private int page;
    final private int perPage;
}
