package com.core_sync.agile_service.kanban_ticket.controller;

import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.kanban_ticket.controller.request_form.CreateKanbanTicketRequestForm;
import com.core_sync.agile_service.kanban_ticket.controller.request_form.ModifyKanbanTicketRequestForm;
import com.core_sync.agile_service.kanban_ticket.controller.response_form.CreateKanbanTicketResponseForm;
import com.core_sync.agile_service.kanban_ticket.controller.response_form.MyTicketResponseForm;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.service.KanbanTicketService;
import com.core_sync.agile_service.kanban_ticket.service.response.CreateKanbanTicketResponse;
import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kanban-ticket")
public class KanbanTicketController {

    final private KanbanTicketService kanbanTicketService;
    final private RedisCacheService redisCacheService;
    final private AccountProfileClient accountProfileClient;

    @PostMapping("/register")
    public CreateKanbanTicketResponseForm registerKanbanTicket (
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateKanbanTicketRequestForm createKanbanTicketRequestForm) {

        log.info("registerKanbanTicket() -> {}", createKanbanTicketRequestForm);
        log.info("authorizationHeader -> {}", authorizationHeader);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        CreateKanbanTicketResponse response = kanbanTicketService.register(createKanbanTicketRequestForm.toCreateKanbanTicketRequest(accountId));

        return CreateKanbanTicketResponseForm.from(response);
    }

    @PutMapping("/modify/{ticketId}")
    public ResponseEntity<CreateKanbanTicketResponseForm> modifyKanbanTicket(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("ticketId") Long ticketId,
            @RequestBody ModifyKanbanTicketRequestForm modifyKanbanTicketRequestForm) {

        log.info("modifyKanbanTicket() -> ticketId: {}, form: {}", ticketId, modifyKanbanTicketRequestForm);
        log.info("Received priority: '{}', status: '{}'", modifyKanbanTicketRequestForm.getPriority(), modifyKanbanTicketRequestForm.getStatus());

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        CreateKanbanTicketResponse response = kanbanTicketService.modify(
                modifyKanbanTicketRequestForm.toModifyKanbanTicketRequest(ticketId), accountId
        );

        return ResponseEntity.ok(CreateKanbanTicketResponseForm.from(response));
    }

    @DeleteMapping("/delete/{ticketId}")
    public ResponseEntity<Void> deleteKanbanTicket(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("ticketId") Long ticketId) {

        log.info("deleteKanbanTicket() -> ticketId: {}", ticketId);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        kanbanTicketService.delete(ticketId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<MyTicketResponseForm>> getMyTickets(
            @RequestHeader("Authorization") String authorizationHeader) {

        log.info("getMyTickets() called");

        try {
            String userToken = authorizationHeader.replace("Bearer ", "").trim();
            log.info("Token extracted: {}", userToken.substring(0, Math.min(10, userToken.length())) + "...");

            Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
            log.info("accountId from Redis: {}", accountId);
            
            if (accountId == null) {
                log.error("Invalid token - accountId is null");
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            // accountId를 accountProfileId로 사용 (DB에서 account_profile_id = accountId)
            Long accountProfileId = accountId;
            log.info("Using accountId as accountProfileId: {}", accountProfileId);

            List<KanbanTicket> myTickets = kanbanTicketService.getMyTickets(accountProfileId);
            log.info("Found {} tickets for accountProfileId: {}", myTickets.size(), accountProfileId);

            // DTO로 변환하여 Lazy Loading 문제 해결
            List<MyTicketResponseForm> response = myTickets.stream()
                    .map(MyTicketResponseForm::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getMyTickets: ", e);
            throw e;
        }
    }
}
