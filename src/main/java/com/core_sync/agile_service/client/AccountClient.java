package com.core_sync.agile_service.client;


import com.core_sync.agile_service.client.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountClient {

    private final RestTemplate restTemplate;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    private String getAccountUrl() {
        return accountServiceUrl + "/account";
    }


    public AccountResponse AccountFindById(Long AccountId) {
        try {
            String url = getAccountUrl() + "/{accountId}";
            log.info("AccountClient - 요청 URL: {}, accountId: {}", url, AccountId);
            AccountResponse response = restTemplate.getForObject(url, AccountResponse.class, AccountId);
            log.info("AccountClient - 응답: {}", response);
            return response;
        } catch (Exception e) {
            log.error("AccountClient - Account 조회 실패: accountId={}, error={}", AccountId, e.getMessage());
            // 실패 시 기본 AccountResponse 반환
            AccountResponse fallback = new AccountResponse();
            fallback.setId(AccountId);
            return fallback;
        }
    }

    public AccountResponse findByEmail(String email) {
        String url = getAccountUrl() + "/email/{email}";
        return restTemplate.getForObject(url, AccountResponse.class, email);
    }
}
