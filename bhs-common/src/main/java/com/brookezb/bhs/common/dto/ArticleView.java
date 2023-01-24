package com.brookezb.bhs.common.dto;

import com.brookezb.bhs.common.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.reactive.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author brooke_zb
 */
@Data
@RegisterForReflection
public class ArticleView {
    private Long aid;
    private UserView user;
    private CategoryView category;
    private List<TagView> tags;
    private String title;
    private String content;
    private boolean commentabled;
    private boolean appreciatabled;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;
    private Integer views;
    private Article.Status status;

    public ArticleView(Long aid,
                       @ProjectedFieldName("user.uid") Long userUid,
                       @ProjectedFieldName("user.avatar") String userAvatar,
                       @ProjectedFieldName("user.name") String userName,
                       @ProjectedFieldName("category.cid") Long categoryCid,
                       @ProjectedFieldName("category.name") String categoryName,
                       String title,
                       String content,
                       boolean commentabled,
                       boolean appreciatabled,
                       LocalDateTime created,
                       LocalDateTime modified,
                       Integer views,
                       Article.Status status) {
        this.aid = aid;
        this.user = new UserView(userUid, userAvatar, userName);
        this.category = new CategoryView(categoryCid, categoryName);
        this.title = title;
        this.content = content;
        this.commentabled = commentabled;
        this.appreciatabled = appreciatabled;
        this.created = created;
        this.modified = modified;
        this.views = views;
        this.status = status;
    }

    /**
     * @author brooke_zb
     */
    @Data
    @RegisterForReflection
    public static class TagView {
        private Long tid;
        private String name;

        public TagView(@ProjectedFieldName("tag.tid") Long tid, @ProjectedFieldName("tag.name") String name) {
            this.tid = tid;
            this.name = name;
        }
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
