package com.core_sync.agile_service.project.repository;

import com.core_sync.agile_service.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p ORDER BY p.id DESC")
    Page<Project> findAllWithWriter(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.id = :projectId")
    Optional<Project> findByIdWithWriter(Long projectId);

    @Query("SELECT p FROM Project p WHERE p.teamId = :teamId")
    List<Project> findByTeamIdWithWriter(@Param("teamId") Long teamId);
    Optional<Project> findByAccountProfileIdAndTitle(Long accountProfileId, String title);
    
    // teamId 리스트로 프로젝트 조회
    @Query("SELECT p FROM Project p WHERE p.teamId IN :teamIds")
    List<Project> findByTeamIdIn(@Param("teamIds") List<Long> teamIds);
    
    // accountProfileId로 프로젝트 조회
    @Query("SELECT p FROM Project p WHERE p.accountProfileId = :accountProfileId")
    List<Project> findByAccountProfileId(@Param("accountProfileId") Long accountProfileId);
}
