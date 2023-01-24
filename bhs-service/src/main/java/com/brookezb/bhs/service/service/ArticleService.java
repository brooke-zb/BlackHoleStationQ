package com.brookezb.bhs.service.service;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.ArticleInfoView;
import com.brookezb.bhs.common.dto.ArticleView;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.service.repository.ArticleRepository;
import com.brookezb.bhs.service.repository.TagRelationRepository;
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

    public Uni<PageInfo<ArticleInfoView>> findListByCategoryId(Long cid, int page) {
        final int queryPage = page - 1; // 页码从0开始
        var query = articleRepository.find("category.cid", cid);

        // 获取总数和分页数据属于两个异步操作，而一个session无法同时执行两个异步操作，所以需要使用chain
        // 所以不能使用Uni.combine().all().unis()
        return query.count()
                .chain(total -> Uni.createFrom().item(new PageInfo<ArticleInfoView>(page, AppConstants.PAGE_SIZE, total)))
                .chain(pageInfo -> query.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(ArticleInfoView.class).list()
                        .chain(articleViewList -> {
                            pageInfo.setList(articleViewList);
                            return Uni.createFrom().item(pageInfo);
                        })
                );
    }
}
