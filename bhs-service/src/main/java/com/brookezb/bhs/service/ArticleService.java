package com.brookezb.bhs.service;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.ArticleInfoView;
import com.brookezb.bhs.common.dto.ArticleTimelineView;
import com.brookezb.bhs.common.dto.ArticleView;
import com.brookezb.bhs.common.entity.Article;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.service.repository.ArticleRepository;
import com.brookezb.bhs.service.repository.TagRelationRepository;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    public Uni<ArticleView> findById(Long id) {
        return articleRepository.find("aid", id)
                .project(ArticleView.class)
                .singleResult()
                .onItem().call(articleView -> tagRelationRepository.find("aid", id)
                        .project(ArticleView.TagView.class)
                        .list()
                        .onItem().invoke(articleView::setTags)
                );

        // 使用panache前的屎山写法
        /*return factory.withSession(session -> session
                .createQuery("""
                        select
                            a.aid as aid,
                            u.uid as uid,
                            u.name as uname,
                            c.cid as cid,
                            c.name as cname,
                            a.title as title,
                            a.content as content,
                            a.commentabled as commentabled,
                            a.appreciatabled as appreciatabled,
                            a.created as created,
                            a.modified as modified,
                            a.views as views,
                            a.status as status
                        from Article a
                            join a.user as u
                            join a.category as c
                        where a.aid = ?1
                        """, Tuple.class)
                .setParameter(1, id)
                .getSingleResult()
                .onItem().ifNotNull().transform(tuple -> ArticleView.builder()
                        .aid(tuple.get("aid", Long.class))
                        .user(ArticleUserView.builder()
                                .uid(tuple.get("uid", Long.class))
                                .name(tuple.get("uname", String.class))
                                .build())
                        .category(ArticleCategoryView.builder()
                                .cid(tuple.get("cid", Long.class))
                                .name(tuple.get("cname", String.class))
                                .build())
                        .title(tuple.get("title", String.class))
                        .content(tuple.get("content", String.class))
                        .commentabled(tuple.get("commentabled", Boolean.class))
                        .appreciatabled(tuple.get("appreciatabled", Boolean.class))
                        .created(tuple.get("created", LocalDateTime.class))
                        .modified(tuple.get("modified", LocalDateTime.class))
                        .views(tuple.get("views", Integer.class))
                        .status(tuple.get("status", Article.Status.class))
                        .build()
                ).call(articleView -> session.createQuery("""
                                select
                                    t.tid as tid,
                                    t.name as name
                                from Article a
                                    join a.tags t
                                where a.aid = ?1
                                """, Tuple.class)
                        .setParameter(1, id)
                        .getResultList()
                        .onItem().ifNotNull().transform(tags -> tags.stream()
                                .map(tag -> ArticleTagView.builder()
                                        .tid(tag.get("tid", Long.class))
                                        .name(tag.get("name", String.class))
                                        .build()
                                ).toList()
                        ).invoke(articleView::setTags)
                )
                .onFailure().recoverWithNull()
        );*/
    }

    public Uni<PageInfo<ArticleInfoView>> findListByCategoryId(Long cid, int page, Article.Status status) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("category.cid = ?1 and status = ?2", Sort.descending("created"), cid, status);

        // 获取总数和分页数据属于两个异步操作，而一个session无法同时执行两个异步操作，所以需要使用chain
        // 所以不能使用Uni.combine().all().unis()
        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(ArticleInfoView.class).list()
                        .chain(articleViewList -> Uni.createFrom().item(new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList)))
                );
    }

    public Uni<PageInfo<ArticleInfoView>> findListByUserId(Long uid, int page, Article.Status status) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("user.uid = ?1 and status = ?2", Sort.descending("created"), uid, status);

        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(ArticleInfoView.class).list()
                        .chain(articleViewList -> Uni.createFrom().item(new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList)))
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
                        .chain(articleViewList -> Uni.createFrom().item(new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList)))
                );
    }

    public Uni<PageInfo<ArticleTimelineView>> findListByTimeline(int page) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("status", Sort.descending("created"), Article.Status.PUBLISHED);

        return query.count()
                .chain(total -> query.page(queryPage, AppConstants.TIMELINE_SIZE)
                        .project(ArticleTimelineView.class).list()
                        .chain(articleViewList -> Uni.createFrom().item(new PageInfo<>(page, AppConstants.PAGE_SIZE, total, articleViewList)))
                );
    }

    public Integer increaseAndGetViews(Long id) {
        return 1;
    }
}
