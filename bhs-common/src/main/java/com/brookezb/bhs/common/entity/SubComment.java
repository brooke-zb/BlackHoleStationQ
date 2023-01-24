package com.brookezb.bhs.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author brooke_zb
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("1")
public class SubComment extends AbstractComment {
    /**
     * 父评论id
     */
    private Long parent;

    /**
     * 回复评论
     */
    private Long reply;

    /**
     * 回复评论昵称
     */
    private String replyname;
}
