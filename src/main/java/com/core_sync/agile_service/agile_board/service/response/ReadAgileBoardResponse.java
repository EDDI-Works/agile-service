package com.core_sync.agile_service.agile_board.service.response;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.project.entity.Project;
import com.core_sync.agile_service.project.service.response.ReadProjectResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ReadAgileBoardResponse {
    final private Long agileBoardId;
    final private Long projectId;
    final private String title;
    final private String writerNickname;
    final private List<Map<String, Object>> kanbanTicketList;
    final private long totalItems;
    final private int totalPages;
    final private AccountProfileResponse accountProfileResponse;

    public static ReadAgileBoardResponse from(AgileBoard agileBoard,
                                           List<KanbanTicket> kanbanTicketList,
                                           long totalItems,
                                           int totalPages,
                                              AccountProfileResponse accountProfileResponse,
                                              AccountProfileClient accountProfileClient
    ) {

        List<Map<String, Object>> kanbanTicketMaps = kanbanTicketList.stream().map(kanbanTicket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", kanbanTicket.getId());
            map.put("title", kanbanTicket.getTitle());
            map.put("description", kanbanTicket.getDescription());
            map.put("status", kanbanTicket.getStatus() != null ? kanbanTicket.getStatus().name() : null);
            map.put("priority", kanbanTicket.getPriority() != null ? kanbanTicket.getPriority().name() : null);
            map.put("domain", kanbanTicket.getDomain());
            map.put("writerId", kanbanTicket.getAccountProfileId());
            map.put("backlogNumber", kanbanTicket.getBacklogNumber());
            
            // 각 티켓의 작성자 닉네임을 개별적으로 조회
            String writerNickname = "User";
            try {
                AccountProfileResponse ticketWriterProfile = accountProfileClient.AccountProfileFindById(kanbanTicket.getAccountProfileId());
                writerNickname = ticketWriterProfile.getNickname();
            } catch (Exception e) {
                writerNickname = "User #" + kanbanTicket.getAccountProfileId();
            }
            map.put("writerNickname", writerNickname);
            
            map.put("createDate", kanbanTicket.getCreateDate());
            map.put("updateDate", kanbanTicket.getUpdateDate());
            map.put("linkedCommitSha", kanbanTicket.getLinkedCommitSha());
            map.put("linkedCommitMessage", kanbanTicket.getLinkedCommitMessage());
            map.put("linkedCommitUrl", kanbanTicket.getLinkedCommitUrl());
            return map;
        }).collect(Collectors.toList());

        return new ReadAgileBoardResponse(
                agileBoard.getId(),
                agileBoard.getProject().getId(),
                agileBoard.getTitle(),
                accountProfileResponse.getNickname(),
                kanbanTicketMaps,
                totalItems,
                totalPages,
                accountProfileResponse
        );
    }
}
