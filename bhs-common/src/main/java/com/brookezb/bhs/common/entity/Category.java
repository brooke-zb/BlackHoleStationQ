package com.brookezb.bhs.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

/**
 * 分类表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("0")
public class Category extends AbstractCategory{

    /**
     * 子分类
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
    @Fetch(FetchMode.SUBSELECT)
    private List<SubCategory> children;
}