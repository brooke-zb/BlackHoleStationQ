package com.brookezb.bhs.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 评论表
 */
@Data
@Entity
@Table(name = "bhs_comment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_child", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorOptions(force = true)
public abstract class AbstractComment {
    /**
     * 评论id
     */
    @Id
    @GeneratedValue(generator = "snowflake")
    private Long coid;

    /**
     * 文章id
     */
    private Long aid;

    /**
     * 评论者id
     */
    private Long uid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像（邮箱hash）
     */
    @Column(insertable = false, updatable = false)
    @Formula("md5(email)")
    private String avatar;

    /**
     * 网址
     */
    private String site;

    /**
     * IP
     */
    private String ip;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    /**
     * 评论状态
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        /**
         * 已发布
         */
        PUBLISHED,
        /**
         * 待审核
         */
        PENDING,
        /**
         * 不可见
         */
        INVISIBLE
    }
}