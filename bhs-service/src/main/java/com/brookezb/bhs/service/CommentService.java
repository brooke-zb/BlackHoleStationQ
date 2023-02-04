package com.brookezb.bhs.service;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.CommentView;
import com.brookezb.bhs.common.entity.AbstractComment;
import com.brookezb.bhs.common.entity.Comment;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.service.repository.CommentRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class CommentService {
    @Inject
    CommentRepository commentRepository;

    public Uni<Comment> findById(Long id) {
        return commentRepository.find("coid", id)
                .singleResult();
    }

    public Uni<PageInfo<CommentView>> findAllByArticleId(Long aid, int page) {
        final int queryPage = page - 1;
        var countQuery = commentRepository.find("select coid from Comment where aid = ?1 and status = ?2", aid, AbstractComment.Status.PUBLISHED);

        return countQuery.count()
                .chain(total -> countQuery.page(queryPage, AppConstants.PAGE_SIZE)
                        .project(Long.class)
                        .list()
                        .chain(idList -> commentRepository.find("""
                                select distinct c
                                from Comment c
                                    left join fetch c.children s
                                where c.coid in ?1
                                    and (s.coid is null or s.status = ?2)
                                """, idList, AbstractComment.Status.PUBLISHED)
                                .stream()
                                .map(CommentView::from)
                                .collect().asList()
                        )
                        .map(commentList -> new PageInfo<>(page, AppConstants.PAGE_SIZE, total, commentList))
                );
    }
}
