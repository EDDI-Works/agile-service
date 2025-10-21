package com.core_sync.agile_service.github.service;

import java.util.Map;

public interface GithubWebhookService {
    void handlePushEvent(Map<String, Object> payload);
    void handlePullRequestEvent(Map<String, Object> payload);
    void handleIssuesEvent(Map<String, Object> payload);
    boolean verifySignature(Map<String, Object> payload, String signature);
}
