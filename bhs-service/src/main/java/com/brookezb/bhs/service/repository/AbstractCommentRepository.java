package com.brookezb.bhs.service.repository;

import com.brookezb.bhs.common.entity.AbstractComment;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class AbstractCommentRepository implements PanacheRepository<AbstractComment> {
}
