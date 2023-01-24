package com.brookezb.bhs.common.dto;

import com.brookezb.bhs.common.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.reactive.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author brooke_zb
 */
@Data
@RegisterForReflection
public class ArticleInfoView {
    private Long aid;
    private UserView user;
    private CategoryView category;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;
    private Integer views;
    private Article.Status status;

    public ArticleInfoView(Long aid,
                           @ProjectedFieldName("user.uid") Long userUid,
                           @ProjectedFieldName("user.avatar") String userAvatar,
                           @ProjectedFieldName("user.name") String userName,
                           @ProjectedFieldName("category.cid") Long categoryCid,
                           @ProjectedFieldName("category.name") String categoryName,
                           String title,
                           LocalDateTime created,
                           LocalDateTime modified,
                           Integer views,
                           Article.Status status) {
        this.aid = aid;
        this.user = new UserView(userUid, userAvatar, userName);
        this.category = new CategoryView(categoryCid, categoryName);
        this.title = title;
        this.created = created;
        this.modified = modified;
        this.views = views;
        this.status = status;
    }

    /**
     * @author brooke_zb
     */
    @Data
    @AllArgsConstructor
    @RegisterForReflection
    public static class UserView {
        private Long uid;
        private String avatar;
        private String name;
    }

    /**
     * @author brooke_zb
     */
    @Data
    @AllArgsConstructor
    @RegisterForReflection
    public static class CategoryView {
        private Long cid;
        private String name;
    }
}
