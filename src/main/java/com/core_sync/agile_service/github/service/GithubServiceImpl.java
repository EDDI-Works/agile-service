package com.core_sync.agile_service.github.service;

import com.core_sync.agile_service.github.service.response.GithubRepositoryListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final RestTemplate restTemplate;
    
    @Value("${github.api-base-url}")
    private String githubApiBaseUrl;

    @Override
    public GithubRepositoryListResponse getRepositories(String accessToken) {
        log.info("GitHub API 호출 시작 - 토큰 길이: {}", accessToken != null ? accessToken.length() : 0);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        log.info("Authorization 헤더: {}", headers.getFirst("Authorization"));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String url = githubApiBaseUrl + "/user/repos?per_page=100&sort=updated";
            log.info("GitHub API URL: {}", url);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub 저장소 조회 실패: " + response.getStatusCode());
            }

            List<GithubRepositoryListResponse.RepositoryInfo> repositories = response.getBody().stream()
                    .map(repo -> new GithubRepositoryListResponse.RepositoryInfo(
                            ((Number) repo.get("id")).longValue(),
                            (String) repo.get("name"),
                            (String) repo.get("full_name"),
                            (String) repo.get("html_url"),
                            (String) repo.get("description"),
                            (Boolean) repo.get("private"),
                            ((Map<String, Object>) repo.get("owner")).get("login").toString()
                    ))
                    .collect(Collectors.toList());

            return new GithubRepositoryListResponse(repositories);

        } catch (Exception e) {
            log.error("GitHub 저장소 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 저장소 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<Map<String, Object>> getCommits(String accessToken, String owner, String repo, int perPage) {
        return getCommits(accessToken, owner, repo, 1, perPage);
    }

    @Override
    public List<Map<String, Object>> getCommits(String accessToken, String owner, String repo, int page, int perPage) {
        log.info("GitHub 커밋 조회 - owner: {}, repo: {}, page: {}, perPage: {}", owner, repo, page, perPage);
        
        try {
            String url = githubApiBaseUrl + "/repos/" + owner + "/" + repo + "/commits?page=" + page + "&per_page=" + perPage;
            log.info("GitHub API URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            
            // accessToken이 있으면 인증 헤더 추가 (비공개 저장소 접근 가능)
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.setBearerAuth(accessToken);
                log.info("GitHub 토큰으로 인증된 요청");
            } else {
                log.info("공개 저장소 요청 (인증 없음)");
            }
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub 커밋 조회 실패: " + response.getStatusCode());
            }

            // 필요한 정보만 추출
            return response.getBody().stream()
                    .map(commit -> {
                        Map<String, Object> commitData = new java.util.HashMap<>();
                        commitData.put("sha", commit.get("sha"));
                        
                        Map<String, Object> commitInfo = (Map<String, Object>) commit.get("commit");
                        commitData.put("message", commitInfo.get("message"));
                        
                        Map<String, Object> author = (Map<String, Object>) commitInfo.get("author");
                        commitData.put("author", Map.of(
                                "name", author.get("name"),
                                "email", author.get("email"),
                                "date", author.get("date")
                        ));
                        
                        commitData.put("url", commit.get("html_url"));
                        
                        return commitData;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("GitHub 커밋 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 커밋 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Map<String, Object> getCommitDetail(String accessToken, String owner, String repo, String sha) {
        log.info("GitHub 커밋 상세 조회 - owner: {}, repo: {}, sha: {}", owner, repo, sha);
        
        try {
            String url = githubApiBaseUrl + "/repos/" + owner + "/" + repo + "/commits/" + sha;
            log.info("GitHub API URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.setBearerAuth(accessToken);
            }
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub 커밋 상세 조회 실패: " + response.getStatusCode());
            }

            return response.getBody();

        } catch (Exception e) {
            log.error("GitHub 커밋 상세 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 커밋 상세 조회 중 오류가 발생했습니다.", e);
        }
    }
}
