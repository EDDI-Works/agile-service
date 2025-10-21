package com.core_sync.agile_service.client;


import com.core_sync.agile_service.client.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        String url = getAccountUrl() + "/{accountId}";
        return restTemplate.getForObject(url, AccountResponse.class, AccountId);
    }

    public AccountResponse findByEmail(String email) {
        String url = getAccountUrl() + "/email/{email}";
        return restTemplate.getForObject(url, AccountResponse.class, email);
    }
}
