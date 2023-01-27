package com.brookezb.bhs.service.service;

import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.service.repository.UserRepository;
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
}
