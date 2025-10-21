package com.core_sync.agile_service.ai.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpenAIServiceImpl implements com.core_sync.agile_service.ai.service.OpenAIService {

    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;

    @Override
    public String generateBacklogFromCommits(List<Map<String, Object>> commits) {
        log.info("AI 백로그 생성 시작 - 커밋 수: {}", commits.size());

        try {
            // OpenAI 서비스 초기화
            OpenAiService service = new OpenAiService(openaiApiKey, Duration.ofSeconds(60));

            // 커밋 정보를 텍스트로 변환
            String commitsText = commits.stream()
                    .map(commit -> {
                        String message = (String) commit.get("message");
                        Map<String, Object> author = (Map<String, Object>) commit.get("author");
                        String authorName = (String) author.get("name");
                        String date = (String) author.get("date");
                        return String.format("- [%s] %s (작성자: %s)", date, message, authorName);
                    })
                    .collect(Collectors.joining("\n"));

            // 프롬프트 생성
            String prompt = String.format(
                    "다음은 GitHub 커밋 내역입니다:\n\n%s\n\n" +
                    "위 커밋 내역을 분석하여 애자일 백로그를 작성해주세요. " +
                    "백로그는 다음 형식으로 작성해주세요:\n\n" +
                    "## 주요 변경사항\n" +
                    "- 변경사항 1\n" +
                    "- 변경사항 2\n\n" +
                    "## 기능 개선\n" +
                    "- 개선사항 1\n" +
                    "- 개선사항 2\n\n" +
                    "## 버그 수정\n" +
                    "- 수정사항 1\n" +
                    "- 수정사항 2\n\n" +
                    "## 다음 작업 제안\n" +
                    "- 제안 1\n" +
                    "- 제안 2\n\n" +
                    "한국어로 작성하고, 구체적이고 명확하게 작성해주세요.",
                    commitsText
            );

            // ChatGPT API 호출
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "당신은 소프트웨어 개발 프로젝트의 애자일 백로그를 작성하는 전문가입니다."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .temperature(0.7)
                    .maxTokens(1000)
                    .build();

            String result = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("AI 백로그 생성 완료");
            return result;

        } catch (Exception e) {
            log.error("AI 백로그 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 백로그 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateDetailedBacklogFromCommit(Map<String, Object> commitDetail) {
        log.info("상세 AI 백로그 생성 시작");

        try {
            OpenAiService service = new OpenAiService(openaiApiKey, Duration.ofSeconds(90));

            // 커밋 기본 정보
            Map<String, Object> commit = (Map<String, Object>) commitDetail.get("commit");
            String message = (String) commit.get("message");
            Map<String, Object> author = (Map<String, Object>) commit.get("author");
            String authorName = (String) author.get("name");
            String date = (String) author.get("date");

            // 파일 변경 정보 추출
            List<Map<String, Object>> files = (List<Map<String, Object>>) commitDetail.get("files");
            StringBuilder filesInfo = new StringBuilder();
            
            if (files != null && !files.isEmpty()) {
                filesInfo.append("\n\n### 변경된 파일 목록:\n");
                for (Map<String, Object> file : files) {
                    String filename = (String) file.get("filename");
                    String status = (String) file.get("status");
                    Integer additions = (Integer) file.get("additions");
                    Integer deletions = (Integer) file.get("deletions");
                    String patch = (String) file.get("patch");
                    
                    filesInfo.append(String.format("\n**파일**: %s\n", filename));
                    filesInfo.append(String.format("**상태**: %s\n", status));
                    filesInfo.append(String.format("**변경**: +%d / -%d 줄\n", additions, deletions));
                    
                    if (patch != null && !patch.isEmpty()) {
                        // diff를 최대 500자로 제한
                        String limitedPatch = patch.length() > 500 ? patch.substring(0, 500) + "..." : patch;
                        filesInfo.append(String.format("**코드 변경:**\n```\n%s\n```\n", limitedPatch));
                    }
                }
            }

            // 프롬프트 생성
            String prompt = String.format(
                    "다음은 GitHub 커밋의 상세 정보입니다:\n\n" +
                    "**커밋 메시지**: %s\n" +
                    "**작성자**: %s\n" +
                    "**날짜**: %s\n" +
                    "%s\n\n" +
                    "위 커밋의 코드 변경사항을 분석하여 처음 보는 개발자가 이해하고 이어서 작업할 수 있도록 상세한 백로그를 작성해주세요.\n\n" +
                    "다음 내용을 포함해주세요:\n\n" +
                    "## 📋 작업 개요\n" +
                    "- 이 커밋에서 수행한 작업의 목적과 배경\n\n" +
                    "## 🔧 주요 변경사항\n" +
                    "- 추가/수정/삭제된 주요 기능\n" +
                    "- 변경된 파일별 핵심 내용\n\n" +
                    "## 💡 중요 메서드 및 클래스\n" +
                    "- 새로 추가되거나 수정된 중요한 메서드/클래스 설명\n" +
                    "- 각 메서드의 역할과 파라미터 설명\n\n" +
                    "## 🔗 연관 작업\n" +
                    "- 이 변경사항과 관련된 다른 부분\n" +
                    "- 영향을 받을 수 있는 기능\n\n" +
                    "## ✅ 다음 작업 제안\n" +
                    "- 이어서 해야 할 작업\n" +
                    "- 개선이 필요한 부분\n\n" +
                    "## 📝 주의사항\n" +
                    "- 이 코드를 수정할 때 주의해야 할 점\n" +
                    "- 테스트해야 할 부분\n\n" +
                    "한국어로 작성하고, 기술적으로 정확하면서도 이해하기 쉽게 작성해주세요.",
                    message, authorName, date, filesInfo.toString()
            );

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "당신은 소프트웨어 개발 프로젝트의 코드 리뷰 및 문서화 전문가입니다. 코드 변경사항을 분석하여 다른 개발자가 쉽게 이해할 수 있도록 상세하고 구조화된 문서를 작성합니다."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .temperature(0.7)
                    .maxTokens(2000)
                    .build();

            String result = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("상세 AI 백로그 생성 완료");
            return result;

        } catch (Exception e) {
            log.error("상세 AI 백로그 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("상세 AI 백로그 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
