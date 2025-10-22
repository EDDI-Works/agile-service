package com.core_sync.agile_service.kanban_ticket.entity;

import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import com.core_sync.agile_service.project.entity.Project;
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
public class KanbanTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;

    @JoinColumn(name = "account_profile_id", nullable = false)
    private Long accountProfileId;
    
    @Column(name = "assignee_id")
    private Long assigneeId;
    
    @Column(name = "backlog_number")
    private Integer backlogNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agile_board_id", nullable = false)
    private AgileBoard agileBoard;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String domain;

    @Lob
    private String description;

    // GitHub 커밋 연결 정보
    private String linkedCommitSha;
    private String linkedCommitMessage;
    private String linkedCommitUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    public KanbanTicket(String title, Long accountProfileId, AgileBoard agileBoard) {
        this.title = title;
        this.accountProfileId = accountProfileId;
        this.agileBoard = agileBoard;
    }

    public KanbanTicket(String title, String description, Status status, Priority priority, String domain, Long accountProfileId, AgileBoard agileBoard) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.domain = domain;
        this.accountProfileId = accountProfileId;
        this.agileBoard = agileBoard;
    }
}
