package com.brookezb.bhs.common.dto;

import lombok.Data;

/**
 * @author brooke_zb
 */
@Data
public class LoginView {
    private String username;
    private String password;
    private boolean rememberMe;
}
