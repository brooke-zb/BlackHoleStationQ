package com.brookezb.bhs.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author brooke_zb
 */
@Data
public class ArticleTimelineView {
    private Long aid;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    public ArticleTimelineView(Long aid, String title, LocalDateTime created) {
        this.aid = aid;
        this.title = title;
        this.created = created;
    }
}
