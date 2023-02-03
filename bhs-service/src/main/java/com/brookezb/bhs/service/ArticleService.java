package com.brookezb.bhs.service;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.ArticleInfoView;
import com.brookezb.bhs.common.dto.ArticleTimelineView;
import com.brookezb.bhs.common.dto.ArticleView;
import com.brookezb.bhs.common.entity.Article;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.service.repository.ArticleRepository;
import com.brookezb.bhs.service.repository.TagRelationRepository;
import io.quarkus.cache.*;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class ArticleService {
    @Inject
    ArticleRepository articleRepository;
    @Inject
    TagRelationRepository tagRelationRepository;

    @CacheName("article-views-cache")
    Cache cache;

    @CacheResult(cacheName = "article-cache")
    public Uni<ArticleView> findById(Long id) {
        return articleRepository.find("aid", id)
                .project(ArticleView.class)
                .singleResult()
                .onItem().call(articleView -> tagRelationRepository.find("aid", id)
                        .project(ArticleView.TagView.class)
                        .list()
                        .onItem().invoke(articleView::setTags)
                );
    }

    public Uni<PageInfo<ArticleInfoView>> findListByCategoryId(Long cid, int page, Article.Status status) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("category.cid = ?1 and status = ?2", Sort.descending("created"), cid, status);

        // 获取总数和分页数据属于两个异步操作，而一个session无法同时执行两个异步操作，所以需要使用chain
        // 所以不能使用Uni.combine().all().unis()
        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(ArticleInfoView.class).list()
                        .map(articleViewList -> new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList))
                );
    }

    public Uni<PageInfo<ArticleInfoView>> findListByUserId(Long uid, int page, Article.Status status) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("user.uid = ?1 and status = ?2", Sort.descending("created"), uid, status);

        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(ArticleInfoView.class).list()
                        .map(articleViewList -> new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList))
                );
    }

    public Uni<PageInfo<ArticleInfoView>> findListByTag(String tag, int page, Article.Status status) {
        final int queryPage = page - 1; // 页码从0开始
        var query = tagRelationRepository.find("""
                select tr.article.aid
                    from TagRelation tr
                where tr.tag.name = ?1
                    and tr.article.status = ?2
                """, Sort.descending("created"), tag, status);

        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(Long.class).list()
                        .chain(aids -> articleRepository.find("aid in ?1", aids).project(ArticleInfoView.class).list())
                        .map(articleViewList -> new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList))
                );
    }

    public Uni<PageInfo<ArticleTimelineView>> findListByTimeline(int page) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("status", Sort.descending("created"), Article.Status.PUBLISHED);

        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.TIMELINE_SIZE)
                        .project(ArticleTimelineView.class).list()
                        .map(articleViewList -> new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList))
                );
    }

    public Uni<Integer> increaseAndGetViews(Long id, String ip) {
        var caffeineCache = cache.as(CaffeineCache.class);
        return caffeineCache.<Long, Set<String>>get(id, _id -> null)
                .onItem().ifNull().continueWith(() -> {
                    var ips = ConcurrentHashMap.<String>newKeySet();
                    caffeineCache.put(id, CompletableFuture.completedFuture(ips));
                    return ips;
                })
                .map(ips -> {
                    ips.add(ip);
                    return ips.size();
                });
    }

    @CacheInvalidate(cacheName = "article-cache")
    public Uni<Integer> updateViews(@CacheKey Long id, int views) {
        return articleRepository.update("views = views + ?1 where aid = ?2", views, id);
    }
}
