package com.core_sync.agile_service.project.entity;

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
@ToString
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;

    @JoinColumn(name = "account_profile_id", nullable = false)
    private Long accountProfileId;

    @Column(name = "team_id")
    private Long teamId;

    @Setter
    @Column(name = "github_repository_url")
    private String githubRepositoryUrl;

    @Setter
    @Column(name = "github_repository_name")
    private String githubRepositoryName;

    @Setter
    @Column(name = "github_owner")
    private String githubOwner;

    public Project(String title, Long accountProfileId) {
        this.title = title;
        this.accountProfileId = accountProfileId;
    }

    public Project(String title, Long accountProfileId, Long teamId) {
        this.title = title;
        this.accountProfileId = accountProfileId;
        this.teamId = teamId;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
