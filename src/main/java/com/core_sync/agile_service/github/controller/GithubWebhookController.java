package com.core_sync.agile_service.github.controller;

import com.core_sync.agile_service.github.service.GithubWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/github/webhook")
@RequiredArgsConstructor
public class GithubWebhookController {

    private final GithubWebhookService webhookService;

    /**
     * GitHub Webhook 이벤트 수신
     * 
     * 로컬 개발: ngrok 등을 사용하여 공개 URL 생성 필요
     * 프로덕션: 실제 공개 도메인 사용
     */
    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody Map<String, Object> payload
    ) {
        log.info("GitHub Webhook 수신 - Event: {}", event);
        log.debug("Payload: {}", payload);

        try {
            // Webhook 시그니처 검증 (보안)
            // if (!webhookService.verifySignature(payload, signature)) {
            //     log.warn("Invalid webhook signature");
            //     return ResponseEntity.status(401).body("Invalid signature");
            // }

            // 이벤트 타입별 처리
            switch (event != null ? event : "") {
                case "push":
                    webhookService.handlePushEvent(payload);
                    break;
                case "pull_request":
                    webhookService.handlePullRequestEvent(payload);
                    break;
                case "issues":
                    webhookService.handleIssuesEvent(payload);
                    break;
                case "ping":
                    log.info("GitHub Webhook ping 수신 - 연결 확인 성공");
                    return ResponseEntity.ok("pong");
                default:
                    log.info("처리되지 않은 이벤트 타입: {}", event);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e) {
            log.error("Webhook 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Webhook processing failed");
        }
    }
}
