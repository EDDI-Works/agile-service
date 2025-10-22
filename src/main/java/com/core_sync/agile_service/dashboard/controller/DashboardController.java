package com.core_sync.agile_service.dashboard.controller;

import com.core_sync.agile_service.kanban_ticket.repository.KanbanTicketRepository;
import com.core_sync.agile_service.project.entity.Project;
import com.core_sync.agile_service.project.repository.ProjectRepository;
import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final ProjectRepository projectRepository;
    private final KanbanTicketRepository kanbanTicketRepository;
    private final RedisCacheService redisCacheService;
    private final RestTemplate restTemplate;
    
    @Value("${hr.service.url}")
    private String hrServiceUrl;
    
    // 사용자의 프로젝트/티켓 통계 조회
    @GetMapping("/project-stats")
    public ResponseEntity<Map<String, Object>> getProjectStats(
            @RequestHeader("Authorization") String token
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        try {
            // HR Service에서 사용자의 팀 목록 조회
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> hrResponse = restTemplate.exchange(
                hrServiceUrl + "/api/team/list",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            // 팀 ID 목록 추출
            List<Long> teamIds = new ArrayList<>();
            List<?> teams = hrResponse.getBody();
            if (teams != null) {
                for (Object teamObj : teams) {
                    if (teamObj instanceof Map) {
                        Map<?, ?> teamMap = (Map<?, ?>) teamObj;
                        Object idObj = teamMap.get("id");
                        if (idObj instanceof Number) {
                            teamIds.add(((Number) idObj).longValue());
                        }
                    }
                }
            }
            
            // 팀 ID로 프로젝트 조회
            List<Project> projects = teamIds.isEmpty() ? 
                new ArrayList<>() : 
                projectRepository.findByTeamIdIn(teamIds);
            
            // 각 프로젝트의 티켓 수 합산
            int totalTickets = 0;
            for (Project project : projects) {
                int ticketCount = kanbanTicketRepository.countByProjectId(project.getId());
                totalTickets += ticketCount;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("projectCount", projects.size());
            response.put("ticketCount", totalTickets);
            
            log.info("프로젝트 통계 조회 - accountId: {}, projectCount: {}, ticketCount: {}", 
                    accountId, projects.size(), totalTickets);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("프로젝트 통계 조회 실패", e);
            Map<String, Object> response = new HashMap<>();
            response.put("projectCount", 0);
            response.put("ticketCount", 0);
            return ResponseEntity.ok(response);
        }
    }
}
