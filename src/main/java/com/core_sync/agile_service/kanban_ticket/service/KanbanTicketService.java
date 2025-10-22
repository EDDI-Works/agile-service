package com.core_sync.agile_service.kanban_ticket.service;

import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.service.request.CreateKanbanTicketRequest;
import com.core_sync.agile_service.kanban_ticket.service.request.ModifyKanbanTicketRequest;
import com.core_sync.agile_service.kanban_ticket.service.response.CreateKanbanTicketResponse;

import java.util.List;

public interface KanbanTicketService {
    CreateKanbanTicketResponse register(CreateKanbanTicketRequest createKanbanTicketRequest);
    CreateKanbanTicketResponse modify(ModifyKanbanTicketRequest modifyKanbanTicketRequest, Long accountId);
    void delete(Long ticketId);
    List<KanbanTicket> getMyTickets(Long accountProfileId);
}
