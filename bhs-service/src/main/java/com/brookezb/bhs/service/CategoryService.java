package com.brookezb.bhs.service;

import com.brookezb.bhs.common.entity.Category;
import com.brookezb.bhs.service.repository.CategoryRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class CategoryService {
    @Inject
    CategoryRepository categoryRepository;

    public Uni<Category> findById(Long id) {
        return categoryRepository.find("cid", id)
                .singleResult();
    }

    public Uni<List<Category>> findAll() {
        return categoryRepository.findAll().list();
    }
}
