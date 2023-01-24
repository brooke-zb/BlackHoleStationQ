package com.brookezb.bhs.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author brooke_zb
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("1")
public class SubCategory extends AbstractCategory {
    /**
     * 父评论id
     */
    private Long parent;
}
