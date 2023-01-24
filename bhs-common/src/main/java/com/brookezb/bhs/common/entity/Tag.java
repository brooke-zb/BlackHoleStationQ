package com.brookezb.bhs.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 标签表
 */
@Data
@Entity
@Table(name = "bhs_tag")
public class Tag {
    /**
     * 标签id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;

    /**
     * 标签名称
     */
    private String name;

    @OneToMany(mappedBy = "keys.tid")
    private List<TagRelation> articles;

    /**
     * 标签热度
     */
    @Transient
    private Integer heat;
}