package com.core_sync.agile_service.client;

import com.core_sync.agile_service.client.response.AccountProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AccountProfileClient {

    private final RestTemplate restTemplate;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    private String getAccountProfileUrl() {
        return accountServiceUrl + "/account-profile";
    }

    public AccountProfileResponse AccountProfileFindById(Long AccountId) {
        String url = getAccountProfileUrl() + "/{accountProfileId}";
        return restTemplate.getForObject(url, AccountProfileResponse.class, AccountId);
    }

}
