package com.brookezb.bhs.security.interceptor;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.security.annotation.PermitAll;
import com.brookezb.bhs.security.annotation.RequireLogin;
import com.brookezb.bhs.security.annotation.RequirePermission;
import io.quarkus.arc.Priority;
import io.quarkus.security.UnauthorizedException;
import io.vertx.ext.web.RoutingContext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

/**
 * @author brooke_zb
 */
@RequirePermission("ignored")
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class RequirePermissionInterceptor {
    @Context
    RoutingContext routing;

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        // RequirePermission注解优先级最高，方法上有该注解时直接生效
        if (requirePermission != null) {
            return checkPermission(context, requirePermission);
        }

        // 方法上没有RequirePermission安全注解，则需检查方法上是否有其他安全注解
        RequireLogin requireLogin = method.getAnnotation(RequireLogin.class);
        PermitAll permitAll = method.getAnnotation(PermitAll.class);
        if (requireLogin == null && permitAll == null) {
            requirePermission = method.getDeclaringClass().getAnnotation(RequirePermission.class);
            return checkPermission(context, requirePermission);
        }
        return context.proceed();
    }

    private Object checkPermission(InvocationContext context, RequirePermission anno) throws Exception {
        var user = routing.<User>get(AppConstants.CONTEXT_USER_KEY);

        if (user == null) {
            throw new UnauthorizedException("未登录，请登录后再操作");
        }

        checkLabel:
        if (anno.relation() == RequirePermission.Relation.OR) {
            // 只要有一个权限满足即可
            for (String permission : anno.value()) {
                if (user.getRole().getPermissions().contains(permission)) {
                    break checkLabel;
                }
            }
            throw new ForbiddenException("您没有权限执行该操作");
        } else {
            // 所有权限都要满足
            for (String permission : anno.value()) {
                if (!user.getRole().getPermissions().contains(permission)) {
                    throw new ForbiddenException("您没有权限执行该操作");
                }
            }
        }

        // 添加已检查标记阻止其他安全拦截器检查
        context.getContextData().put(AppConstants.CONTEXT_SECURITY_CHECKED_KEY, true);
        return context.proceed();
    }
}
