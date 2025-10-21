package com.core_sync.agile_service.ai.service;

import java.util.List;
import java.util.Map;

public interface OpenAIService {
    String generateBacklogFromCommits(List<Map<String, Object>> commits);
    String generateDetailedBacklogFromCommit(Map<String, Object> commitDetail);
}
