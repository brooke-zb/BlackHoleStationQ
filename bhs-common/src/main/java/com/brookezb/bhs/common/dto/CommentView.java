package com.brookezb.bhs.common.dto;

import com.brookezb.bhs.common.entity.Comment;
import com.brookezb.bhs.common.entity.SubComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author brooke_zb
 */
@Data
@Accessors(chain = true)
public class CommentView {
    private Long coid;
    private Long aid;
    private String nickname;
    private String avatar;
    private String site;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private List<SubCommentView> children;

    public static CommentView from(Comment comment) {
        return new CommentView()
        .setCoid(comment.getCoid())
        .setAid(comment.getAid())
        .setNickname(comment.getNickname())
        .setAvatar(comment.getAvatar())
        .setSite(comment.getSite())
        .setContent(comment.getContent())
        .setCreated(comment.getCreated())
        .setChildren(comment.getChildren().stream().map(SubCommentView::from).collect(Collectors.toList()));
    }

    @Data
    @Accessors(chain = true)
    static class SubCommentView {
        private Long coid;
        private Long aid;
        private String nickname;
        private String avatar;
        private String site;
        private String content;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created;
        private Long parent;
        private Long reply;
        private String replyname;

        public static SubCommentView from(SubComment comment) {
            return new SubCommentView()
            .setCoid(comment.getCoid())
            .setAid(comment.getAid())
            .setNickname(comment.getNickname())
            .setAvatar(comment.getAvatar())
            .setSite(comment.getSite())
            .setContent(comment.getContent())
            .setCreated(comment.getCreated())
            .setParent(comment.getParent())
            .setReply(comment.getReply())
            .setReplyname(comment.getReplyname());
        }
    }
}
