package com.brookezb.bhs.security.annotation;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * @author brooke_zb
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireLogin {
}
