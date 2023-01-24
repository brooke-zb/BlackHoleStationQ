package com.brookezb.bhs.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * 角色表
 */
@Data
@Entity
@Table(name = "bhs_role")
public class Role {
    /**
     * 角色id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rid;

    /**
     * 角色名
     */
    private String name;

    /**
     * 拥有权限
     */
    @Column(name = "permission")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bhs_role_permission", joinColumns = @JoinColumn(name = "rid"))
    private Set<String> permissions;
}