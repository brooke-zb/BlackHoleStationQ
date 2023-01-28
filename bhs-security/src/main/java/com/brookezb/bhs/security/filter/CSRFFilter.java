package com.brookezb.bhs.security.filter;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.common.util.CookieUtil;
import com.brookezb.bhs.security.config.CSRFConfig;
import com.brookezb.bhs.security.util.CSRFUtil;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author brooke_zb
 */
@Provider
public class CSRFFilter {
    private static final String HEADER = "X-CSRF-TOKEN";
    private static final String CONTEXT_PASS_KEY = "bhs.security.csrf.pass";

    @Inject
    CSRFConfig config;

    @Context
    RoutingContext routing;

    @ServerRequestFilter
    public Response validateToken(ContainerRequestContext requestContext) throws IOException {
        // 检查路径
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        if (config.excludePaths().contains(path)) {
            return null;
        }
        boolean isInclude = false;
        for (var prefix : config.includePrefixes()) {
            if (path.startsWith(prefix)) {
                isInclude = true;
                break;
            }
        }
        if (!isInclude) {
            return null;
        }

        // 不拦截GET, OPTIONS及HEAD请求
        switch (requestContext.getMethod()) {
            case "GET", "OPTIONS", "HEAD" -> {
                return null;
            }
        }

        // 无token时拦截
        if (!requestContext.getHeaders().containsKey(HEADER)) {
            return Response.status(Response.Status.FORBIDDEN).entity(R.fail("缺少CSRF令牌")).build();
        }

        // 校验token
        String token = requestContext.getHeaderString(HEADER);
        if (!CSRFUtil.validateToken(token, config.secret())) {
            // 重置token
            routing.response().addCookie(CookieUtil.from(HEADER, "").setMaxAge(0));
            return Response.status(Response.Status.FORBIDDEN).entity(R.fail("无效的CSRF令牌")).build();
        }
        routing.put(CONTEXT_PASS_KEY, true);
        return null;
    }

    /**
     * 通过检查cookie决定是否需要设置CSRF token
     */
    @ServerResponseFilter
    public void issueToken() throws IOException {
        // 在登录用户没有可用token的情况下生成新的token
        var routingData = routing.data();
        if (routingData.containsKey(AppConstants.CONTEXT_USER_KEY) && !routingData.containsKey(CONTEXT_PASS_KEY)) {
            var token = CSRFUtil.createToken(config.secret());
            routing.response().addCookie(CookieUtil.from(HEADER, token));
        }
    }
}
