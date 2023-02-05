package com.brookezb.bhs.common.dto;

import com.brookezb.bhs.common.entity.AbstractComment;
import com.brookezb.bhs.common.entity.Comment;
import com.brookezb.bhs.common.entity.SubComment;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author brooke_zb
 */
@Data
public class CommentCreateView {
    private Long uid;
    private Long aid;
    private String nickname;
    private String ip;
    private String email;
    private String site;
    private String content;
    private Long reply;

    public AbstractComment toEntity() {
        if (reply != null) {
            var sc = new SubComment();
            sc.setUid(uid);
            sc.setAid(aid);
            sc.setNickname(nickname);
            sc.setIp(ip);
            sc.setEmail(email);
            sc.setSite(site);
            sc.setContent(content);
            sc.setReply(reply);
            sc.setCreated(LocalDateTime.now());
            return sc;
        } else {
            var c = new Comment();
            c.setUid(uid);
            c.setAid(aid);
            c.setNickname(nickname);
            c.setIp(ip);
            c.setEmail(email);
            c.setSite(site);
            c.setContent(content);
            c.setCreated(LocalDateTime.now());
            return c;
        }
    }

}
