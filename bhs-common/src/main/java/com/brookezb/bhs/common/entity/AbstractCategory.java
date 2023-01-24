package com.brookezb.bhs.common.entity;

import lombok.Data;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;

/**
 * @author brooke_zb
 */
@Data
@Entity
@Table(name = "bhs_category")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_child", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorOptions(force = true)
public abstract class AbstractCategory {
    /**
     * 分类id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cid;

    /**
     * 分类名称
     */
    private String name;
}
