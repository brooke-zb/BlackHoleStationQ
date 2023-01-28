package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.LoginView;
import com.brookezb.bhs.common.dto.PasswordUpdateView;
import com.brookezb.bhs.common.dto.UserUpdateView;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.common.util.CookieUtil;
import com.brookezb.bhs.service.service.UserService;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
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

    @Context
    RoutingContext routingContext;
    @Context
    HttpServerResponse response;

    /**
     * 登录接口
     *
     * @param loginBody 用户名和密码
     */
    @POST
    @Path("/token")
    public Uni<R<Void>> login(LoginView loginBody) {
        return userService.checkUser(loginBody.getUsername(), loginBody.getPassword())
                .onItem().ifNull().failWith(() -> new UnauthorizedException("用户名或密码错误"))
                .map(user -> {
                    // 设置用户以便发放CSRF token
                    routingContext.put(AppConstants.CONTEXT_USER_KEY, user);

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
                    return R.okWithMsg("登录成功");
                });
    }

    /**
     * 退出登录接口
     */
    @DELETE
    @Path("/token")
    public Uni<R<Void>> logout() {
        response.addCookie(CookieUtil.from("Authorization", "")
                .setMaxAge(0L)
                .setHttpOnly(true)
        );
        routingContext.remove(AppConstants.CONTEXT_USER_KEY);
        return Uni.createFrom().item(R.okWithMsg("成功退出登录"));
    }

    /**
     * 获取当前登录用户信息
     */
    @GET
    @Path("")
    public Uni<R<User>> info(@RestCookie String Authorization) {
        if (Authorization == null) {
            return Uni.createFrom().item(R.fail("未登录"));
        }
        User currentUser = routingContext.get(AppConstants.CONTEXT_USER_KEY);
        if (currentUser == null) {
            return Uni.createFrom().item(R.fail("登录已过期"));
        }
        return Uni.createFrom().item(R.ok(currentUser));
    }

    /**
     * 更新用户信息
     */
    @PUT
    @Path("")
    public Uni<R<Void>> update(UserUpdateView updateBody) {
        return userService.update(routingContext.<User>get(AppConstants.CONTEXT_USER_KEY).getUid(), updateBody)
                .replaceWith(() -> R.okWithMsg("信息更新成功"));
    }

    /**
     * 更改用户密码
     *
     * @param updateBody 旧密码和新密码
     */
    @PATCH
    @Path("/password")
    public Uni<R<Void>> updatePassword(PasswordUpdateView updateBody) { // TODO: validate request body
        return userService.updatePassword(routingContext.<User>get(AppConstants.CONTEXT_USER_KEY).getUid(), updateBody)
                .replaceWith(() -> R.okWithMsg("密码更新成功"));
    }
}