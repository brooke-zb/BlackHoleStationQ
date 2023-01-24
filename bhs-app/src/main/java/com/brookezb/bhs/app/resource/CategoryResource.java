package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.entity.Category;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.service.CategoryService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @Path("")
    public Uni<R<List<Category>>> getCategories() {
        return categoryService.findAll()
                .onItem().ifNotNull().transform(R::ok)
                .onItem().ifNull().continueWith(R::fail);
    }
}
