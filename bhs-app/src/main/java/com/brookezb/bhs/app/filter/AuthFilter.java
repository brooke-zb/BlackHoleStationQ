package com.brookezb.bhs.app.filter;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.util.CookieUtil;
import com.brookezb.bhs.service.service.UserService;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

/**
 * @author brooke_zb
 */
@Provider
@PreMatching
public class AuthFilter {
    @Context
    RoutingContext routingContext;

    @Inject
    UserService userService;

    @CacheName("temp-auth-token-cache")
    Cache tempAuthTokenCache;
    @CacheName("persistent-auth-token-cache")
    Cache persistentAuthTokenCache;

    @ServerRequestFilter
    public Uni<Void> filter(ContainerRequestContext request) {
        Cookie cookie = request.getCookies().get("Authorization");
        if (cookie == null) {
            return Uni.createFrom().voidItem();
        }
        String token = cookie.getValue();

        return tempAuthTokenCache.as(CaffeineCache.class).<String, Long>get(token, key -> null)
                .onItem().ifNull()
                .switchTo(() -> persistentAuthTokenCache.get(token, key -> null))
                .onItem().ifNotNull()
                .transformToUni(userService::findById)
                .invoke(user -> {
                    if (user == null || !user.isEnabled()) {
                        // 移除无效的cookie
                        routingContext.response().addCookie(CookieUtil.from("Authorization", "")
                                .setMaxAge(0L)
                                .setHttpOnly(true)
                        );
                        return;
                    }
                    routingContext.put(AppConstants.CONTEXT_USER_KEY, user);
                })
                .replaceWithVoid();
    }
}
