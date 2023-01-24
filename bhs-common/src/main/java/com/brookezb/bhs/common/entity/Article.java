package com.brookezb.bhs.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author brooke_zb
 */
@Data
@Entity
@Table(name = "bhs_article")
public class Article {
    /**
     * 文章id
     */
    @Id
    @GeneratedValue(generator = "snowflake", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "snowflake", strategy = "com.brookezb.bhs.common.util.SnowflakeIdGenerator")
    private Long aid;

    /**
     * 作者id
     */
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    /**
     * 分类id
     */
    @ManyToOne
    @JoinColumn(name = "cid")
    private AbstractCategory category;

    /**
     * 文章标签
     */
    @OneToMany(mappedBy = "keys.aid")
    @Fetch(FetchMode.SUBSELECT)
    private List<TagRelation> tags;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 开启评论
     */
    private boolean commentabled = true;

    /**
     * 开启赞赏
     */
    private boolean appreciatabled = true;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;

    /**
     * 草稿状态
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 浏览量
     */
    private Integer views;

    /**
     * 评论
     */
    @OneToMany(mappedBy = "aid")
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> comments;

    public enum Status {
        PUBLISHED, DRAFT, INVISIBLE
    }
}
