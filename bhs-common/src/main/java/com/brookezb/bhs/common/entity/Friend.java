package com.brookezb.bhs.common.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author brooke_zb
 */
@Data
@Entity
@Table(name = "bhs_friend")
public class Friend {
    /**
     * 友链id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 站点链接
     */
    private String link;

    /**
     * 图标链接
     */
    private String avatar;

    /**
     * 网站介绍
     */
    private String description;
}
