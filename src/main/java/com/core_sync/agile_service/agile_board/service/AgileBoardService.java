package com.core_sync.agile_service.agile_board.service;

import com.core_sync.agile_service.agile_board.service.request.CreateAgileBoardRequest;
import com.core_sync.agile_service.agile_board.service.response.CreateAgileBoardResponse;
import com.core_sync.agile_service.agile_board.service.response.ReadAgileBoardResponse;

public interface AgileBoardService {
    ReadAgileBoardResponse read(Long agileBoardId, Integer page, Integer perPage, Long accountId);
    CreateAgileBoardResponse register(CreateAgileBoardRequest createAgileBoardRequest);
}
