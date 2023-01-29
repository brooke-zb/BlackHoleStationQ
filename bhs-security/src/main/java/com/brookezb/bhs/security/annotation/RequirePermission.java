package com.brookezb.bhs.security.annotation;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * @author brooke_zb
 */
@Inherited
@InterceptorBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    @Nonbinding
    String[] value();

    @Nonbinding
    Relation relation() default Relation.OR;

    enum Relation {
        OR, AND
    }
}
