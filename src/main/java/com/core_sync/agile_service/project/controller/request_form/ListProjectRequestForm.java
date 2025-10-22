package com.core_sync.agile_service.project.controller.request_form;

import com.core_sync.agile_service.project.service.request.ListProjectRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ListProjectRequestForm {
    final private int page;
    final private int perPage;

    public ListProjectRequest toListProjectRequest() {
        return new ListProjectRequest(page, perPage);
    }
}
