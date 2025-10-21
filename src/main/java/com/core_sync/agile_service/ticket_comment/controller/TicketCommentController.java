package com.core_sync.agile_service.ticket_comment.controller;

import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import com.core_sync.agile_service.ticket_comment.controller.request_form.CreateTicketCommentRequestForm;
import com.core_sync.agile_service.ticket_comment.controller.request_form.ModifyTicketCommentRequestForm;
import com.core_sync.agile_service.ticket_comment.service.TicketCommentService;
import com.core_sync.agile_service.ticket_comment.service.request.CreateTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.request.ModifyTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.response.TicketCommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket-comment")
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;
    private final RedisCacheService redisCacheService;

    @PostMapping("/register")
    public ResponseEntity<TicketCommentResponse> register(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateTicketCommentRequestForm requestForm) {
        
        log.info("register comment -> {}", requestForm);
        
        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        // accountId를 accountProfileId로 사용
        Long accountProfileId = accountId;
        
        CreateTicketCommentRequest serviceRequest = new CreateTicketCommentRequest();
        serviceRequest.setTicketId(requestForm.getTicketId());
        serviceRequest.setContent(requestForm.getContent());
        
        TicketCommentResponse response = ticketCommentService.register(serviceRequest, accountProfileId);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/modify/{commentId}")
    public ResponseEntity<TicketCommentResponse> modify(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long commentId,
            @RequestBody ModifyTicketCommentRequestForm requestForm) {
        
        log.info("modify comment -> commentId: {}, form: {}", commentId, requestForm);
        
        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        Long accountProfileId = accountId;
        
        ModifyTicketCommentRequest serviceRequest = new ModifyTicketCommentRequest();
        serviceRequest.setContent(requestForm.getContent());
        
        TicketCommentResponse response = ticketCommentService.modify(commentId, serviceRequest, accountProfileId);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long commentId) {
        
        log.info("delete comment -> commentId: {}", commentId);
        
        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        Long accountProfileId = accountId;
        
        ticketCommentService.delete(commentId, accountProfileId);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/{ticketId}")
    public ResponseEntity<List<TicketCommentResponse>> getCommentsByTicketId(@PathVariable Long ticketId) {
        log.info("get comments by ticketId: {}", ticketId);
        List<TicketCommentResponse> comments = ticketCommentService.getCommentsByTicketId(ticketId);
        return ResponseEntity.ok(comments);
    }
}
