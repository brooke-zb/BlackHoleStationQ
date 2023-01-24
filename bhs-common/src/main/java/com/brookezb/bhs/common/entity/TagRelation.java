package com.brookezb.bhs.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author brooke_zb
 */
@Data
@Entity
@Table(name = "bhs_tag_relation")
public class TagRelation {
    @EmbeddedId
    private Id keys;

    @OneToOne
    @JoinColumn(name = "aid", insertable = false, updatable = false)
    private Article article;

    @OneToOne
    @JoinColumn(name = "tid", insertable = false, updatable = false)
    private Tag tag;

    @Data
    public static class Id implements Serializable {
        private Long aid;
        private Long tid;
    }
}
