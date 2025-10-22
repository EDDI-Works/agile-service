package com.core_sync.agile_service.project.controller.response_form;

import com.core_sync.agile_service.project.service.response.CreateProjectResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateProjectResponseForm {
    private final Long id;
    private final String title;
    private final String writerNickname;
    private final LocalDateTime createDate;

    public static CreateProjectResponseForm from(CreateProjectResponse response) {
        return new CreateProjectResponseForm(
                response.getId(),
                response.getTitle(),
                response.getWriterNickname(),
                response.getCreateDate()
        );
    }
}
