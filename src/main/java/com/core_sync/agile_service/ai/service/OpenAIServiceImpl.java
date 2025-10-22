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
                    "위 커밋 내역을 분석하여 개발자가 바로 이해하고 작업할 수 있도록 구조화된 백로그를 작성해주세요.\n\n" +
                    "## 개요\n" +
                    "이 커밋들의 전체적인 목적과 배경을 2-3문장으로 요약해주세요.\n\n" +
                    "---\n\n" +
                    "## 1. 주요 변경사항\n\n" +
                    "### 1.1 추가된 기능\n" +
                    "- **기능명**: 구체적인 설명\n" +
                    "- **기능명**: 구체적인 설명\n\n" +
                    "### 1.2 수정된 기능\n" +
                    "- **기능명**: 변경 전 → 변경 후\n" +
                    "- **기능명**: 변경 전 → 변경 후\n\n" +
                    "### 1.3 삭제된 기능\n" +
                    "- **기능명**: 삭제 이유\n\n" +
                    "---\n\n" +
                    "## 2. 기술적 구현 내용\n\n" +
                    "### 2.1 주요 클래스/메서드\n" +
                    "**클래스명.메서드명()**\n" +
                    "- 역할: 이 메서드가 하는 일\n" +
                    "- 파라미터: 입력값 설명\n" +
                    "- 반환값: 출력값 설명\n\n" +
                    "### 2.2 API 엔드포인트 (있는 경우)\n" +
                    "**HTTP_METHOD /api/경로**\n" +
                    "- 요청: 필요한 데이터\n" +
                    "- 응답: 반환되는 데이터\n" +
                    "- 역할: API의 목적\n\n" +
                    "### 2.3 데이터베이스 변경 (있는 경우)\n" +
                    "- 테이블명: 변경 내용\n\n" +
                    "---\n\n" +
                    "## 3. 작업 흐름\n\n" +
                    "단계별로 어떤 순서로 작업이 진행되는지 설명해주세요:\n" +
                    "1. 첫 번째 단계\n" +
                    "2. 두 번째 단계\n" +
                    "3. 세 번째 단계\n\n" +
                    "---\n\n" +
                    "## 4. 테스트 방법\n\n" +
                    "이 변경사항을 테스트하는 방법:\n" +
                    "```bash\n" +
                    "# 테스트 명령어 또는 API 호출 예시\n" +
                    "```\n\n" +
                    "**예상 결과**: 정상 동작 시 어떤 결과가 나와야 하는지\n\n" +
                    "---\n\n" +
                    "## To-do Checklist\n\n" +
                    "- [x] 완료된 작업 1\n" +
                    "- [x] 완료된 작업 2\n" +
                    "- [ ] 추가로 필요한 작업 1\n" +
                    "- [ ] 추가로 필요한 작업 2\n\n" +
                    "---\n\n" +
                    "## 참고 사항\n\n" +
                    "- 이 코드를 수정할 때 주의할 점\n" +
                    "- 관련 문서나 이슈 링크 (있다면)\n\n" +
                    "한국어로 작성하고, 마크다운 형식을 정확히 지켜주세요. 각 섹션은 구체적이고 실용적으로 작성해주세요.",
                    commitsText
            );

            // ChatGPT API 호출
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "당신은 소프트웨어 개발 프로젝트의 기술 문서 작성 전문가입니다. 커밋 내역을 분석하여 개발자가 즉시 이해하고 작업을 이어갈 수 있도록 구조화되고 실용적인 백로그를 작성합니다. 마크다운 형식을 정확히 지키고, 각 섹션은 구체적이고 명확하게 작성합니다."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .temperature(0.5)
                    .maxTokens(2500)
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
                    "위 커밋의 코드 변경사항을 분석하여 처음 보는 개발자가 바로 이해하고 작업을 이어갈 수 있도록 구조화된 백로그를 작성해주세요.\n\n" +
                    "## 개요\n" +
                    "이 커밋에서 수행한 작업의 목적과 배경을 2-3문장으로 요약해주세요.\n\n" +
                    "---\n\n" +
                    "## 1. 주요 변경사항\n\n" +
                    "### 1.1 추가된 코드\n" +
                    "**파일명**: `경로/파일명`\n" +
                    "- 추가된 내용: 구체적인 설명\n" +
                    "- 목적: 왜 추가했는지\n\n" +
                    "### 1.2 수정된 코드\n" +
                    "**파일명**: `경로/파일명`\n" +
                    "- 변경 전: 기존 동작\n" +
                    "- 변경 후: 새로운 동작\n" +
                    "- 이유: 왜 수정했는지\n\n" +
                    "### 1.3 삭제된 코드\n" +
                    "**파일명**: `경로/파일명`\n" +
                    "- 삭제된 내용: 무엇을 삭제했는지\n" +
                    "- 이유: 왜 삭제했는지\n\n" +
                    "---\n\n" +
                    "## 2. 기술적 구현 내용\n\n" +
                    "### 2.1 주요 클래스/메서드\n" +
                    "```java\n" +
                    "클래스명.메서드명(파라미터 타입)\n" +
                    "```\n" +
                    "- **역할**: 이 메서드가 하는 일\n" +
                    "- **파라미터**: 입력값과 의미\n" +
                    "- **반환값**: 출력값과 의미\n" +
                    "- **핵심 로직**: 내부에서 어떤 처리를 하는지\n\n" +
                    "### 2.2 API 엔드포인트 (있는 경우)\n" +
                    "**HTTP_METHOD** `/api/경로`\n" +
                    "- **요청 Body**: 필요한 데이터 형식\n" +
                    "  ```json\n" +
                    "  { \"key\": \"value\" }\n" +
                    "  ```\n" +
                    "- **응답**: 반환되는 데이터\n" +
                    "- **역할**: 이 API가 하는 일\n\n" +
                    "### 2.3 데이터베이스 변경 (있는 경우)\n" +
                    "- **테이블명**: 변경 내용 (컬럼 추가/수정/삭제)\n" +
                    "- **쿼리 예시**: 실제 사용되는 SQL\n\n" +
                    "---\n\n" +
                    "## 3. 작업 흐름\n\n" +
                    "이 커밋의 코드가 실행되는 순서를 단계별로 설명해주세요:\n\n" +
                    "1. **첫 번째 단계**: 무엇이 시작되는지\n" +
                    "2. **두 번째 단계**: 어떤 처리가 일어나는지\n" +
                    "3. **세 번째 단계**: 최종 결과는 무엇인지\n\n" +
                    "---\n\n" +
                    "## 4. 테스트 방법\n\n" +
                    "이 변경사항을 테스트하는 구체적인 방법:\n\n" +
                    "```bash\n" +
                    "# API 테스트 예시\n" +
                    "curl -X POST https://example.com/api/endpoint \\\n" +
                    "  -H \"Content-Type: application/json\" \\\n" +
                    "  -d '{\"key\": \"value\"}'\n" +
                    "```\n\n" +
                    "**예상 결과**:\n" +
                    "- 정상 동작 시: 어떤 응답이 나와야 하는지\n" +
                    "- 에러 발생 시: 어떤 에러 메시지가 나올 수 있는지\n\n" +
                    "---\n\n" +
                    "## 5. 영향 범위\n\n" +
                    "### 5.1 연관된 기능\n" +
                    "- 이 변경으로 영향받는 다른 기능들\n\n" +
                    "### 5.2 주의사항\n" +
                    "- 이 코드를 수정할 때 반드시 확인해야 할 것\n" +
                    "- 다른 개발자가 놓치기 쉬운 부분\n\n" +
                    "---\n\n" +
                    "## To-do Checklist\n\n" +
                    "- [x] 이 커밋에서 완료된 작업\n" +
                    "- [ ] 추가로 필요한 작업 (있다면)\n" +
                    "- [ ] 개선이 필요한 부분 (있다면)\n\n" +
                    "---\n\n" +
                    "## 참고 자료\n\n" +
                    "- 관련 문서나 이슈 링크 (있다면)\n" +
                    "- 참고한 라이브러리나 API 문서\n\n" +
                    "한국어로 작성하고, 마크다운 형식을 정확히 지켜주세요. 코드 블록은 언어를 명시하고, 각 섹션은 실제 코드 내용을 기반으로 구체적으로 작성해주세요.",
                    message, authorName, date, filesInfo.toString()
            );

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "당신은 소프트웨어 개발 프로젝트의 기술 문서 작성 전문가입니다. 커밋의 코드 변경사항을 깊이 분석하여 처음 보는 개발자도 즉시 이해하고 작업을 이어갈 수 있도록 구조화되고 실용적인 백로그를 작성합니다. 마크다운 형식을 정확히 지키고, 코드 예시와 테스트 방법을 반드시 포함하며, 각 섹션은 실제 코드 내용을 기반으로 구체적으로 작성합니다."));
            messages.add(new ChatMessage("user", prompt));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(messages)
                    .temperature(0.5)
                    .maxTokens(3000)
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
