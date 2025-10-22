package com.core_sync.agile_service.ticket_comment.entity;

import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "account_profile_id", nullable = false)
    private Long accountProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanban_ticket_id", nullable = false)
    private KanbanTicket kanbanTicket;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    public TicketComment(String content, Long accountProfileId, KanbanTicket kanbanTicket) {
        this.content = content;
        this.accountProfileId = accountProfileId;
        this.kanbanTicket = kanbanTicket;
    }
}
