package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.entity.Category;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.CategoryService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author brooke_zb
 */
@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    @Inject
    CategoryService categoryService;

    @GET
    @Path("/{id:\\d+}")
    public Uni<R<Category>> findById(Long id) {
        return categoryService.findById(id)
                .map(R::ok)
                .onFailure(NoResultException.class).transform(ex -> new NotFoundException("未找到该分类"));
    }

    @GET
    @Path("")
    public Uni<R<List<Category>>> getCategoryList() {
        return categoryService.findAll()
                .map(R::ok);
    }
}
