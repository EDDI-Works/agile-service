package com.core_sync.agile_service.ticket_comment.service;

import com.core_sync.agile_service.ticket_comment.service.request.CreateTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.request.ModifyTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.response.TicketCommentResponse;

import java.util.List;

public interface TicketCommentService {
    TicketCommentResponse register(CreateTicketCommentRequest request, Long accountProfileId);
    TicketCommentResponse modify(Long commentId, ModifyTicketCommentRequest request, Long accountProfileId);
    void delete(Long commentId, Long accountProfileId);
    List<TicketCommentResponse> getCommentsByTicketId(Long ticketId);
}
