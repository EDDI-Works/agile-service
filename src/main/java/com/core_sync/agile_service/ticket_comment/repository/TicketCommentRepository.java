package com.core_sync.agile_service.ticket_comment.repository;

import com.core_sync.agile_service.ticket_comment.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByKanbanTicketIdOrderByCreateDateAsc(Long kanbanTicketId);
}
