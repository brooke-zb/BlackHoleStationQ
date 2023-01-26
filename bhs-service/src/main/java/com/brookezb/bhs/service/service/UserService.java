package com.brookezb.bhs.service.service;

import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.service.repository.UserRepository;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
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

    public Uni<User> checkUser(String username, String password) {
        return userRepository.find("name", username)
                .firstResult()
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
