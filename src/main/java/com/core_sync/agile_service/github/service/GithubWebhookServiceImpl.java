package com.core_sync.agile_service.github.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubWebhookServiceImpl implements GithubWebhookService {

    @Value("${github.webhook-secret}")
    private String webhookSecret;

    @Override
    public void handlePushEvent(Map<String, Object> payload) {
        log.info("Push 이벤트 처리 시작");
        
        try {
            // Repository 정보
            @SuppressWarnings("unchecked")
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            String repoName = (String) repository.get("full_name");
            
            // Commit 정보
            @SuppressWarnings("unchecked")
            Map<String, Object> headCommit = (Map<String, Object>) payload.get("head_commit");
            String commitMessage = headCommit != null ? (String) headCommit.get("message") : "N/A";
            String commitId = headCommit != null ? (String) headCommit.get("id") : "N/A";
            
            // Pusher 정보
            @SuppressWarnings("unchecked")
            Map<String, Object> pusher = (Map<String, Object>) payload.get("pusher");
            String pusherName = pusher != null ? (String) pusher.get("name") : "Unknown";
            
            log.info("Push 이벤트 - Repository: {}, Commit: {}, Message: {}, Pusher: {}", 
                    repoName, commitId, commitMessage, pusherName);
            
            // TODO: 프로젝트와 연동된 저장소인지 확인
            // TODO: 커밋 정보를 프로젝트 활동 로그에 저장
            // TODO: 관련 티켓이 있으면 자동으로 업데이트
            
        } catch (Exception e) {
            log.error("Push 이벤트 처리 중 오류: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handlePullRequestEvent(Map<String, Object> payload) {
        log.info("Pull Request 이벤트 처리 시작");
        
        try {
            String action = (String) payload.get("action");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
            String prTitle = (String) pullRequest.get("title");
            Integer prNumber = (Integer) pullRequest.get("number");
            String prState = (String) pullRequest.get("state");
            
            log.info("Pull Request 이벤트 - Action: {}, PR #{}: {}, State: {}", 
                    action, prNumber, prTitle, prState);
            
            // TODO: PR 정보를 프로젝트에 반영
            // TODO: PR이 머지되면 관련 티켓 상태 자동 업데이트
            
        } catch (Exception e) {
            log.error("Pull Request 이벤트 처리 중 오류: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handleIssuesEvent(Map<String, Object> payload) {
        log.info("Issues 이벤트 처리 시작");
        
        try {
            String action = (String) payload.get("action");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> issue = (Map<String, Object>) payload.get("issue");
            String issueTitle = (String) issue.get("title");
            Integer issueNumber = (Integer) issue.get("number");
            String issueState = (String) issue.get("state");
            
            log.info("Issues 이벤트 - Action: {}, Issue #{}: {}, State: {}", 
                    action, issueNumber, issueTitle, issueState);
            
            // TODO: GitHub Issue를 Kanban Ticket으로 자동 생성
            // TODO: Issue 상태 변경 시 Ticket 상태도 동기화
            
        } catch (Exception e) {
            log.error("Issues 이벤트 처리 중 오류: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(Map<String, Object> payload, String signature) {
        // Webhook Secret이 설정되지 않은 경우 (개발 환경)
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            log.warn("Webhook Secret이 설정되지 않았습니다. 시그니처 검증을 건너뜁니다.");
            return true;
        }
        
        if (signature == null || signature.isEmpty()) {
            log.warn("Webhook 시그니처가 없습니다");
            return false;
        }
        
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                webhookSecret.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            mac.init(secretKeySpec);
            
            byte[] hash = mac.doFinal(payload.toString().getBytes(StandardCharsets.UTF_8));
            String expectedSignature = "sha256=" + bytesToHex(hash);
            
            boolean isValid = signature.equals(expectedSignature);
            if (!isValid) {
                log.warn("Webhook 시그니처 검증 실패");
            }
            return isValid;
            
        } catch (Exception e) {
            log.error("시그니처 검증 중 오류: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
