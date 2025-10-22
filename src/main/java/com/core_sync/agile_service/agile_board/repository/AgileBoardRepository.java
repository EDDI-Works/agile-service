package com.core_sync.agile_service.agile_board.repository;


import com.core_sync.agile_service.agile_board.entity.AgileBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgileBoardRepository extends JpaRepository<AgileBoard, Long> {
    @Query("SELECT ab FROM AgileBoard ab " +
            "JOIN FETCH ab.project p " +
            "WHERE p.id = :projectId")
    Page<AgileBoard> findAllByProjectId(Long projectId, Pageable pageable);

    @Query("SELECT ab FROM AgileBoard ab " +
            "JOIN FETCH ab.project p " +
            "WHERE p.id = :projectId")
    java.util.List<AgileBoard> findAllByProjectId(Long projectId);

    @Query("SELECT ab FROM AgileBoard ab WHERE ab.id = :agileBoardId")
    Optional<AgileBoard> findByIdWithWriter(Long agileBoardId);
}
