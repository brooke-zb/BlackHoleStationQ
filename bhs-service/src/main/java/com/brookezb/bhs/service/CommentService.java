package com.brookezb.bhs.service;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.CommentCreateView;
import com.brookezb.bhs.common.dto.CommentView;
import com.brookezb.bhs.common.entity.AbstractComment;
import com.brookezb.bhs.common.entity.Comment;
import com.brookezb.bhs.common.entity.SubComment;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.mail.MailService;
import com.brookezb.bhs.service.exception.ServiceQueryException;
import com.brookezb.bhs.service.repository.*;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author brooke_zb
 */
@ApplicationScoped
public class CommentService {
    @Inject
    AbstractCommentRepository abstractCommentRepository;
    @Inject
    CommentRepository commentRepository;
    @Inject
    SubCommentRepository subCommentRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    SensitiveWordBs sensitiveWordService;
    @Inject
    MailService mailService;

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

    /**
     * 保存评论
     *
     * @return 发表成功返回true, 进入审核流程则返回false
     * @throws ServiceQueryException 保存失败, 通常是参数错误
     */
    public Uni<Boolean> insert(CommentCreateView createView) {
        var comment = createView.toEntity();
        comment.setStatus(AbstractComment.Status.PUBLISHED);

        // 检查文章是否存在
        return commentRepository.find("select 1 from Article where aid = ?1", comment.getAid())
                .project(Integer.class)
                .firstResult()
                .onItem().ifNull().failWith(() -> new ServiceQueryException("评论文章不存在"))
                .chain(ignored -> {
                    // 检查子评论回复的评论是否存在
                    if (comment instanceof SubComment sc) {
                        return abstractCommentRepository.findById(sc.getReply())
                                .chain(reply -> {
                                    if (reply == null || reply.getStatus() != AbstractComment.Status.PUBLISHED || !reply.getAid().equals(sc.getAid())) {
                                        return Uni.createFrom().failure(new ServiceQueryException("回复的评论不存在"));
                                    }
                                    return Uni.createFrom().item(reply);
                                });
                    }
                    return Uni.createFrom().nullItem();
                })
                .chain(reply -> {
                    // 检查评论内容是否合法
                    var content = comment.getContent();
                    if (content.length() > AppConstants.MAX_COMMENT_LENGTH) {
                        return Uni.createFrom().failure(new ServiceQueryException("评论内容过长"));
                    }
                    final boolean isPublished;
                    if (comment.getUid() == null && (sensitiveWordService.contains(content) || sensitiveWordService.contains(comment.getNickname()))) {
                        comment.setStatus(AbstractComment.Status.PENDING);
                        isPublished = false;
                    } else {
                        isPublished = true;
                        comment.setStatus(AbstractComment.Status.PUBLISHED);
                    }
                    // 子评论设置
                    if (comment instanceof SubComment sc) {
                        if (reply instanceof SubComment subReply) {
                            sc.setParent(subReply.getParent());
                            sc.setReply(subReply.getCoid());
                        } else {
                            sc.setParent(reply.getCoid());
                            sc.setReply(null);
                        }
                        sc.setReplyname(reply.getNickname());
                        // 保存评论
                        return subCommentRepository.persistAndFlush(sc).replaceWith(isPublished);
                    }
                    return commentRepository.persistAndFlush((Comment) comment).replaceWith(isPublished);
                })
                .call(isPublished -> userRepository.find("select user from Article where aid = ?1", comment.getAid())
                        .firstResult()
                        .invoke(user -> {
                            // 发送邮件
                            if (isPublished) {
                                mailService.sendReplyMail(user.getMail(), comment.getNickname(), comment.getAid());
                            } else {
                                mailService.sendAuditMail();
                            }
                        })
                );
    }
}
