package com.brookezb.bhs.common.dto;

import lombok.Data;

/**
 * @author brooke_zb
 */
@Data
public class PasswordUpdateView {
    private String oldPassword;
    private String newPassword;
}
