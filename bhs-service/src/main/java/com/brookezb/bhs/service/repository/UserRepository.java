package com.brookezb.bhs.service.repository;

import com.brookezb.bhs.common.entity.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
