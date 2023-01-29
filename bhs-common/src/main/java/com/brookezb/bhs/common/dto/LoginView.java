package com.brookezb.bhs.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author brooke_zb
 */
@Data
public class LoginView {
    @NotNull(message = "用户名不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
    private boolean rememberMe;
}
