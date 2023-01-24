package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.dto.ArticleInfoView;
import com.brookezb.bhs.common.dto.ArticleView;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.service.ArticleService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/article")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {
    @Inject
    ArticleService articleService;

    @GET
    @Path("/{id:\\d+}")
    public Uni<R<ArticleView>> findById(Long id) {
        return articleService.findById(id)
                .onItem().ifNotNull().transform(R::ok)
                .onItem().ifNull().continueWith(R::fail);
    }

    @GET
    @Path("/category/{cid:\\d+}")
    public Uni<R<PageInfo<ArticleInfoView>>> findListByCategoryId(Long cid, @RestQuery int page) {
        return articleService.findListByCategoryId(cid, page)
                .onItem().ifNotNull().transform(R::ok)
                .onItem().ifNull().continueWith(R::fail);
    }
}