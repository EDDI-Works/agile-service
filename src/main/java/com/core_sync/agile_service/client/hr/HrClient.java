package com.core_sync.agile_service.client.hr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HrClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${hr.service.url}")
    private String hrServiceUrl;
    
    private String getHrTeamUrl() {
        return hrServiceUrl + "/api/team";
    }
    
    // 팀 생성
    public Map<String, Object> createTeam(String token, Map<String, Object> request) {
        String url = getHrTeamUrl() + "/create";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", token);
        
        org.springframework.http.HttpEntity<Map<String, Object>> entity = 
            new org.springframework.http.HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, Map.class);
    }
    
    // 팀 목록 조회
    public List<Map<String, Object>> getTeamList(String token) {
        String url = getHrTeamUrl() + "/list";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", token);
        
        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
        
        return restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            List.class
        ).getBody();
    }
    
    // 팀 상세 조회
    public Map<String, Object> getTeam(Long teamId) {
        String url = getHrTeamUrl() + "/" + teamId;
        return restTemplate.getForObject(url, Map.class);
    }
    
    // 팀 멤버 검증
    public boolean validateTeamMember(String token, Long teamId) {
        try {
            String url = getHrTeamUrl() + "/" + teamId + "/validate-member";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", token);
            
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            
            Map<String, Boolean> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Map.class
            ).getBody();
            
            return response != null && response.getOrDefault("isMember", false);
        } catch (Exception e) {
            log.error("팀 멤버 검증 실패: {}", e.getMessage());
            return false;
        }
    }
    
    // 팀장 여부 확인
    public boolean isTeamLeader(String token, Long teamId) {
        try {
            String url = getHrTeamUrl() + "/" + teamId + "/is-leader";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", token);
            
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            
            Map<String, Boolean> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Map.class
            ).getBody();
            
            return response != null && response.getOrDefault("isLeader", false);
        } catch (Exception e) {
            log.error("팀장 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    // 팀 멤버 초대
    public void inviteMember(String token, Long teamId, Long accountId) {
        String url = getHrTeamUrl() + "/" + teamId + "/invite";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", token);
        
        Map<String, Long> request = Map.of("accountId", accountId);
        org.springframework.http.HttpEntity<Map<String, Long>> entity = 
            new org.springframework.http.HttpEntity<>(request, headers);
        
        restTemplate.postForObject(url, entity, Map.class);
    }
    
    // 팀 멤버 목록 조회
    public List<Map<String, Object>> getTeamMembers(Long teamId) {
        String url = getHrTeamUrl() + "/" + teamId + "/members";
        return restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            null,
            List.class
        ).getBody();
    }
}
