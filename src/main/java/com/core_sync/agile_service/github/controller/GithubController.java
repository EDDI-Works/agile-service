package com.core_sync.agile_service.github.controller;

import com.core_sync.agile_service.ai.service.OpenAIService;
import com.core_sync.agile_service.github.service.GithubService;
import com.core_sync.agile_service.github.service.response.GithubRepositoryListResponse;
import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;
    private final RedisCacheService redisCacheService;
    private final OpenAIService openAIService;

    @GetMapping("/repositories")
    public ResponseEntity<?> getRepositories(
            @RequestHeader("Authorization") String token
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        log.info("User Token: {}", userToken);
        
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        log.info("Account ID: {}", accountId);

        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰입니다."));
        }

        // Redis에서 GitHub Access Token 가져오기
        // 키 형식: "github:token:{accountId}"
        String githubTokenKey = "github:token:" + accountId;
        String githubAccessToken = redisCacheService.getValueByKey(githubTokenKey, String.class);
        
        log.info("GitHub Access Token 조회 - key: {}, 존재 여부: {}", githubTokenKey, githubAccessToken != null);
        
        if (githubAccessToken != null) {
            log.info("GitHub Access Token 길이: {}", githubAccessToken.length());
            if (githubAccessToken.length() > 10) {
                log.info("GitHub Access Token 앞 10자: {}", githubAccessToken.substring(0, 10));
            }
        }
        
        if (githubAccessToken == null) {
            log.warn("GitHub 토큰 없음 - accountId: {}", accountId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "GitHub 인증이 필요합니다. 프로젝트 설정에서 GitHub 계정을 먼저 연동해주세요."));
        }

        GithubRepositoryListResponse response = githubService.getRepositories(githubAccessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/commits/{owner}/{repo}")
    public ResponseEntity<?> getCommits(
            @RequestHeader("Authorization") String token,
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        log.info("GitHub 커밋 조회 - owner: {}, repo: {}, perPage: {}", owner, repo, perPage);
        
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰입니다."));
        }

        // Redis에서 GitHub Access Token 가져오기
        String githubTokenKey = "github:token:" + accountId;
        String githubAccessToken = redisCacheService.getValueByKey(githubTokenKey, String.class);
        
        if (githubAccessToken == null) {
            log.warn("GitHub 토큰 없음 - accountId: {}", accountId);
            // 공개 저장소는 토큰 없이도 조회 가능하도록 시도
        }
        
        try {
            List<Map<String, Object>> commits = githubService.getCommits(githubAccessToken, owner, repo, perPage);
            return ResponseEntity.ok(commits);
        } catch (Exception e) {
            log.error("GitHub 커밋 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "커밋 내역을 가져오는데 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/generate-backlog/{owner}/{repo}")
    public ResponseEntity<?> generateBacklog(
            @RequestHeader("Authorization") String token,
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestParam(value = "per_page", defaultValue = "30") int perPage
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        log.info("AI 백로그 생성 - owner: {}, repo: {}, perPage: {}", owner, repo, perPage);
        
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰입니다."));
        }

        // Redis에서 GitHub Access Token 가져오기
        String githubTokenKey = "github:token:" + accountId;
        String githubAccessToken = redisCacheService.getValueByKey(githubTokenKey, String.class);
        
        if (githubAccessToken == null) {
            log.warn("GitHub 토큰 없음 - accountId: {}", accountId);
        }
        
        try {
            // 커밋 내역 가져오기
            List<Map<String, Object>> commits = githubService.getCommits(githubAccessToken, owner, repo, perPage);
            
            if (commits.isEmpty()) {
                return ResponseEntity.ok(Map.of("backlog", "커밋 내역이 없어 백로그를 생성할 수 없습니다."));
            }
            
            // AI로 백로그 생성
            String backlog = openAIService.generateBacklogFromCommits(commits);
            
            return ResponseEntity.ok(Map.of("backlog", backlog));
        } catch (Exception e) {
            log.error("AI 백로그 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "백로그 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/generate-detailed-backlog/{owner}/{repo}/{sha}")
    public ResponseEntity<?> generateDetailedBacklog(
            @RequestHeader("Authorization") String token,
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("sha") String sha
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        log.info("상세 AI 백로그 생성 - owner: {}, repo: {}, sha: {}", owner, repo, sha);
        
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 토큰입니다."));
        }

        // Redis에서 GitHub Access Token 가져오기
        String githubTokenKey = "github:token:" + accountId;
        String githubAccessToken = redisCacheService.getValueByKey(githubTokenKey, String.class);
        
        if (githubAccessToken == null) {
            log.warn("GitHub 토큰 없음 - accountId: {}", accountId);
        }
        
        try {
            // 커밋 상세 정보 가져오기 (diff 포함)
            Map<String, Object> commitDetail = githubService.getCommitDetail(githubAccessToken, owner, repo, sha);
            
            // AI로 상세 백로그 생성
            String backlog = openAIService.generateDetailedBacklogFromCommit(commitDetail);
            
            return ResponseEntity.ok(Map.of("backlog", backlog));
        } catch (Exception e) {
            log.error("상세 AI 백로그 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "상세 백로그 생성에 실패했습니다: " + e.getMessage()));
        }
    }
}
