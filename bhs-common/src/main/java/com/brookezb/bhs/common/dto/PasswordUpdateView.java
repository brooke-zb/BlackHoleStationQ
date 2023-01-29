package com.brookezb.bhs.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author brooke_zb
 */
@Data
public class PasswordUpdateView {
    @NotNull(message = "旧密码不能为空")
    private String oldPassword;
    @NotNull(message = "新密码不能为空")
    private String newPassword;
}
