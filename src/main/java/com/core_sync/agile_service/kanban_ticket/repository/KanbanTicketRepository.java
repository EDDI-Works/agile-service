package com.core_sync.agile_service.kanban_ticket.repository;

import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KanbanTicketRepository extends JpaRepository<KanbanTicket, Long> {
    @Query("SELECT kt FROM KanbanTicket kt " +
            "JOIN FETCH kt.agileBoard ab " +
            "WHERE ab.id = :agileBoardId " +
            "ORDER BY kt.backlogNumber DESC, kt.createDate DESC")
    Page<KanbanTicket> findAllByAgileBoardId(Long agileBoardId, Pageable pageable);
    
    @Query("SELECT COUNT(kt) FROM KanbanTicket kt WHERE kt.agileBoard.id = :agileBoardId")
    long countByAgileBoardId(Long agileBoardId);
    
    @Query("DELETE FROM KanbanTicket kt WHERE kt.agileBoard.id = :agileBoardId")
    @org.springframework.data.jpa.repository.Modifying
    void deleteAllByAgileBoardId(Long agileBoardId);
    
    @Query("SELECT COUNT(kt) FROM KanbanTicket kt WHERE kt.agileBoard.project.id = :projectId")
    int countByProjectId(Long projectId);
    
    // 내 티켓 조회 (accountProfileId 또는 assigneeId로)
    @Query("SELECT kt FROM KanbanTicket kt " +
            "JOIN FETCH kt.agileBoard ab " +
            "WHERE kt.accountProfileId = :accountProfileId OR kt.assigneeId = :accountProfileId")
    List<KanbanTicket> findAllByAccountProfileIdOrAssigneeId(Long accountProfileId);
}
