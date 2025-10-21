package com.core_sync.agile_service.agile_board.service.response;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;

import com.core_sync.agile_service.client.response.AccountProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateAgileBoardResponse {
    private final Long id;
    private final String title;
    private final String writerNickname;
    private final LocalDateTime createDate;
    private final AccountProfileResponse accountProfileResponse;
    private final Long projectId;

    public static CreateAgileBoardResponse from(AgileBoard agileBoard, AccountProfileResponse accountProfileResponse, Long projectId)  {
        return new CreateAgileBoardResponse(
                agileBoard.getId(),
                agileBoard.getTitle(),
                accountProfileResponse.getNickname(),
                agileBoard.getCreateDate(),
                accountProfileResponse,
                projectId
        );
    }
}
