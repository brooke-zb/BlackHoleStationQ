package com.brookezb.bhs.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * 用户表
 */
@Data
@Entity
@Table(name = "bhs_user")
@JsonIgnoreProperties(value = "password", allowSetters = true)
public class User {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    /**
     * 角色
     */
    @ManyToOne
    @JoinColumn(name = "rid")
    private Role role;

    /**
     * 用户名
     */
    @Column(unique = true)
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    @Column(unique = true)
    private String mail;

    /**
     * 头像（邮箱hash）
     */
    @Formula("md5(mail)")
    private String avatar;

    /**
     * 主页链接
     */
    private String link;

    /**
     * 是否启用
     */
    private boolean enabled;
}