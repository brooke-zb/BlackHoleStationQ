package com.brookezb.bhs.service.repository;

import com.brookezb.bhs.common.entity.TagRelation;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class TagRelationRepository implements PanacheRepositoryBase<TagRelation, TagRelation.Id> {
}
