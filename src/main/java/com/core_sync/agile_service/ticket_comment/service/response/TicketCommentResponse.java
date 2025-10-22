package com.core_sync.agile_service.ticket_comment.service.response;

import com.core_sync.agile_service.ticket_comment.entity.TicketComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TicketCommentResponse {
    private Long id;
    private String content;
    private Writer writer;
    private Long ticketId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Writer {
        private Long id;
        private String nickname;

        public Writer(Long id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }
    }

    public TicketCommentResponse(TicketComment comment, String writerNickname) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = new Writer(comment.getAccountProfileId(), writerNickname);
        this.ticketId = comment.getKanbanTicket().getId();
        this.createDate = comment.getCreateDate();
        this.updateDate = comment.getUpdateDate();
    }
}
