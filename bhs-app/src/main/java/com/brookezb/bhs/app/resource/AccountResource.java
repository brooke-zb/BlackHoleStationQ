package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.LoginView;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.common.util.CookieUtil;
import com.brookezb.bhs.service.service.UserService;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.RestCookie;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    @Inject
    UserService userService;

    @CacheName("temp-auth-token-cache")
    Cache tempAuthTokenCache;
    @CacheName("persistent-auth-token-cache")
    Cache persistentAuthTokenCache;

    @POST
    @Path("/token")
    public Uni<R<?>> login(LoginView loginBody, HttpServerResponse response) {
        return userService.checkUser(loginBody.getUsername(), loginBody.getPassword())
                .onItem().ifNull().failWith(() -> new RuntimeException("用户名或密码错误"))
                .map(user -> {
                    var uuid = UUID.randomUUID();
                    var cookie = CookieUtil.from("Authorization", uuid.toString())
                            .setHttpOnly(true);
                    if (loginBody.isRememberMe()) {
                        persistentAuthTokenCache.as(CaffeineCache.class).put(uuid.toString(), CompletableFuture.completedFuture(user.getUid()));
                        cookie.setMaxAge(60 * 60 * 24 * 7L);
                    } else {
                        tempAuthTokenCache.as(CaffeineCache.class).put(uuid.toString(), CompletableFuture.completedFuture(user.getUid()));
                    }
                    response.addCookie(cookie);
                    return R.ok(user);
                });
    }

    @DELETE
    @Path("/token")
    public Uni<R<?>> logout() {
        return Uni.createFrom().item(R.ok());
    }

    @GET
    @Path("")
    public Uni<R<User>> info(@RestCookie String Authorization, @Context RoutingContext routingContext, HttpServerResponse response) {
        if (Authorization == null) {
            return Uni.createFrom().item(R.fail("未登录"));
        }
        User currentUser = routingContext.get(AppConstants.CONTEXT_USER_KEY);
        if (currentUser == null) {
            response.addCookie(CookieUtil.from("Authorization", "")
                    .setMaxAge(0L)
                    .setHttpOnly(true)
            );
            return Uni.createFrom().item(R.fail("登录已过期"));
        }
        return Uni.createFrom().item(R.ok(currentUser));
    }
}