package com.brookezb.bhs.security.interceptor;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.security.annotation.PermitAll;
import com.brookezb.bhs.security.annotation.RequireLogin;
import io.quarkus.arc.Priority;
import io.quarkus.logging.Log;
import io.quarkus.security.UnauthorizedException;
import io.vertx.ext.web.RoutingContext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

/**
 * @author brooke_zb
 */
@RequireLogin
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 1)
public class RequireLoginInterceptor {
    @Context
    RoutingContext routing;

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        if (context.getContextData().containsKey(AppConstants.CONTEXT_SECURITY_CHECKED_KEY)) {
            return context.proceed();
        }

        Method method = context.getMethod();
        RequireLogin requireLogin = method.getAnnotation(RequireLogin.class);

        // 到这一步RequireLogin注解优先级最高，方法上有该注解时直接生效
        if (requireLogin != null) {
            return checkLogin(context);
        }

        // 方法上没有其他安全注解，则requireLogin生效
        PermitAll permitAll = method.getAnnotation(PermitAll.class);
        if (permitAll == null) {
            return checkLogin(context);
        }
        return context.proceed();
    }

    private Object checkLogin(InvocationContext context) throws Exception {
        Log.info("checking login");
        var user = routing.<User>get(AppConstants.CONTEXT_USER_KEY);
        if (user == null) {
            throw new UnauthorizedException("未登录，请登录后再操作");
        }
        return context.proceed();
    }
}
