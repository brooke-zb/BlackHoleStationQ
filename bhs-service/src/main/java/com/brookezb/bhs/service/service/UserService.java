package com.brookezb.bhs.service.service;

import com.brookezb.bhs.common.dto.UserUpdateView;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.service.exception.ServiceQueryException;
import com.brookezb.bhs.service.repository.UserRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    /**
     * 获取与用户名和密码匹配的用户
     *
     * @param username 用户名
     * @param password 密码
     * @return 匹配的用户，如果没有则返回null
     */
    public Uni<User> checkUser(String username, String password) {
        return userRepository.find("name", username)
                .firstResult()
                // 由于BCrypt算法耗时较长，会阻塞eventloop，所以放在worker线程中执行
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().ifNotNull().transformToUni(user -> {
                    if (BCrypt.checkpw(password, user.getPassword())) {
                        return Uni.createFrom().item(user);
                    } else {
                        return Uni.createFrom().nullItem();
                    }
                });
    }

    @CacheResult(cacheName = "user-cache")
    public Uni<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @CacheInvalidate(cacheName = "user-cache")
    public Uni<Void> update(@CacheKey Long id, UserUpdateView user) {
        return userRepository.findById(id)
                .onItem().ifNotNull()
                .call(updateUser -> userRepository.find("select 1 from User u where u.name = ?1 and u.uid != ?2", user.getName(), updateUser.getUid())
                        .project(Integer.class)
                        .firstResult()
                        .onItem().ifNotNull().failWith(() -> new ServiceQueryException("该用户名已注册，请更换一个"))
                )
                .call(updateUser -> userRepository.find("select 1 from User u where u.mail = ?1 and u.uid != ?2", user.getMail(), updateUser.getUid())
                        .project(Integer.class)
                        .firstResult()
                        .onItem().ifNotNull().failWith(() -> new ServiceQueryException("该邮箱已被注册，请更换一个"))
                )
                .chain(u -> {
                    if (user.getName() != null) {
                        u.setName(user.getName());
                    }
                    if (user.getMail() != null) {
                        u.setMail(user.getMail());
                    }
                    if (user.getLink() != null) {
                        u.setLink(user.getLink());
                    }
                    return userRepository.persistAndFlush(u);
                })
                .replaceWithVoid();
    }
}
