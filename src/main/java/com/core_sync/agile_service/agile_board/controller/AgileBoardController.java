package com.core_sync.agile_service.agile_board.controller;

import com.core_sync.agile_service.agile_board.controller.request_form.CreateAgileBoardRequestForm;
import com.core_sync.agile_service.agile_board.controller.response_form.CreateAgileBoardResponseForm;
import com.core_sync.agile_service.agile_board.controller.response_form.ReadAgileBoardResponseForm;
import com.core_sync.agile_service.agile_board.service.AgileBoardService;
import com.core_sync.agile_service.agile_board.service.response.CreateAgileBoardResponse;
import com.core_sync.agile_service.agile_board.service.response.ReadAgileBoardResponse;
import com.core_sync.agile_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/agile-board")
public class AgileBoardController {

    final private AgileBoardService agileBoardService;
    final private RedisCacheService redisCacheService;

    @GetMapping("/read/{id}")
    public ReadAgileBoardResponseForm readAgileBoard(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("id") Long agileBoardId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer perPage) {

        log.info("readAgileBoard(): {}", agileBoardId);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        ReadAgileBoardResponse response = agileBoardService.read(agileBoardId, page, perPage,accountId);
        return ReadAgileBoardResponseForm.from(response);
    }

    @PostMapping("/register")
    public CreateAgileBoardResponseForm registerAgileBoard (
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateAgileBoardRequestForm createAgileBoardRequestForm) {

        log.info("registerAgileBoard() -> {}", createAgileBoardRequestForm);
        log.info("authorizationHeader -> {}", authorizationHeader);

        String userToken = authorizationHeader.replace("Bearer ", "").trim();

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        CreateAgileBoardResponse response = agileBoardService.register(createAgileBoardRequestForm.toCreateAgileBoardRequest(accountId));

        return CreateAgileBoardResponseForm.from(response);
    }
}
