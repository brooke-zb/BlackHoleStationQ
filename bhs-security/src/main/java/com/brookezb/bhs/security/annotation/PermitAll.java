package com.brookezb.bhs.security.annotation;

import java.lang.annotation.*;

/**
 * @author brooke_zb
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermitAll {
}
