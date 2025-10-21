package com.core_sync.agile_service.ticket_comment.service;

import com.core_sync.agile_service.client.AccountProfileClient;
import com.core_sync.agile_service.client.response.AccountProfileResponse;
import com.core_sync.agile_service.kanban_ticket.entity.KanbanTicket;
import com.core_sync.agile_service.kanban_ticket.repository.KanbanTicketRepository;
import com.core_sync.agile_service.ticket_comment.entity.TicketComment;
import com.core_sync.agile_service.ticket_comment.repository.TicketCommentRepository;
import com.core_sync.agile_service.ticket_comment.service.request.CreateTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.request.ModifyTicketCommentRequest;
import com.core_sync.agile_service.ticket_comment.service.response.TicketCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final KanbanTicketRepository kanbanTicketRepository;
    private final AccountProfileClient accountProfileClient;

    @Override
    @Transactional
    public TicketCommentResponse register(CreateTicketCommentRequest request, Long accountProfileId) {
        KanbanTicket ticket = kanbanTicketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("티켓을 찾을 수 없습니다."));

        TicketComment comment = new TicketComment(
                request.getContent(),
                accountProfileId,
                ticket
        );

        TicketComment savedComment = ticketCommentRepository.save(comment);

        // 작성자 닉네임 조회
        String writerNickname = getWriterNickname(accountProfileId);

        return new TicketCommentResponse(savedComment, writerNickname);
    }

    @Override
    @Transactional
    public TicketCommentResponse modify(Long commentId, ModifyTicketCommentRequest request, Long accountProfileId) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAccountProfileId().equals(accountProfileId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        TicketComment updatedComment = ticketCommentRepository.save(comment);

        String writerNickname = getWriterNickname(accountProfileId);

        return new TicketCommentResponse(updatedComment, writerNickname);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long accountProfileId) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getAccountProfileId().equals(accountProfileId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        ticketCommentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketCommentResponse> getCommentsByTicketId(Long ticketId) {
        List<TicketComment> comments = ticketCommentRepository.findByKanbanTicketIdOrderByCreateDateAsc(ticketId);

        return comments.stream()
                .map(comment -> {
                    String writerNickname = getWriterNickname(comment.getAccountProfileId());
                    return new TicketCommentResponse(comment, writerNickname);
                })
                .collect(Collectors.toList());
    }

    private String getWriterNickname(Long accountProfileId) {
        try {
            AccountProfileResponse profile = accountProfileClient.AccountProfileFindById(accountProfileId);
            return profile.getNickname();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
