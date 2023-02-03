package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.dto.ArticleInfoView;
import com.brookezb.bhs.common.dto.ArticleTimelineView;
import com.brookezb.bhs.common.dto.ArticleView;
import com.brookezb.bhs.common.entity.Article;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.ArticleService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/article")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {
    @Inject
    ArticleService articleService;

    @Context
    HttpServerRequest request;

    @GET
    @Path("/{id:\\d+}")
    public Uni<R<ArticleView>> findById(Long id) {
        return articleService.findById(id)
                .chain(articleView -> {
                    if (articleView.getStatus() != Article.Status.PUBLISHED) {
                        return Uni.createFrom().failure(new NotFoundException("文章不存在或已被删除"));
                    }
                    return Uni.createFrom().item(articleView);
                })
                .map(ArticleView::copy)
                .call(articleView -> articleService.increaseAndGetViews(id, request.remoteAddress().host())
                        .invoke(viewsInCache -> articleView.setViews(articleView.getViews() + viewsInCache))
                )
                .map(R::ok)
                .onFailure(NoResultException.class).transform(ex -> new NotFoundException("文章不存在或已被删除"));
    }

    @GET
    @Path("/category/{cid:\\d+}")
    public Uni<R<PageInfo<ArticleInfoView>>> findListByCategoryId(Long cid, @RestQuery @DefaultValue("1") int page) {
        return articleService.findListByCategoryId(cid, page, Article.Status.PUBLISHED).map(R::ok);
    }

    @GET
    @Path("/user/{uid:\\d+}")
    public Uni<R<PageInfo<ArticleInfoView>>> findListByUserId(Long uid, @RestQuery @DefaultValue("1") int page) {
        return articleService.findListByUserId(uid, page, Article.Status.PUBLISHED).map(R::ok);
    }

    @GET
    @Path("/tag/{tag}")
    public Uni<R<PageInfo<ArticleInfoView>>> findListByTag(String tag, @RestQuery @DefaultValue("1") int page) {
        return articleService.findListByTag(tag, page, Article.Status.PUBLISHED).map(R::ok);
    }

    @GET
    @Path("/timeline")
    public Uni<R<PageInfo<ArticleTimelineView>>> findListByTimeline(@RestQuery @DefaultValue("1") int page) {
        return articleService.findListByTimeline(page).map(R::ok);
    }
}