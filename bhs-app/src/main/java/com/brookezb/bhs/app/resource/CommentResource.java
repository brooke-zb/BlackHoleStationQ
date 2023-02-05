package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.constant.AppConstants;
import com.brookezb.bhs.common.dto.CommentCreateView;
import com.brookezb.bhs.common.dto.CommentView;
import com.brookezb.bhs.common.entity.User;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.CommentService;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author brooke_zb
 */
@Path("/comment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {
    @Inject
    CommentService commentService;

    @GET
    public Uni<R<PageInfo<CommentView>>> findPageByArticleId(@RestQuery @DefaultValue("1") @Min(value = 1, message = "页数不能小于1") int page, @RestQuery long aid) {
        return commentService.findAllByArticleId(aid, page)
                .map(R::ok);
    }

    @POST
    public Uni<R<Void>> addComment(CommentCreateView createView, @Context RoutingContext routingContext) {
        if (routingContext.data().containsKey(AppConstants.CONTEXT_USER_KEY)) {
            createView.setUid(routingContext.<User>get(AppConstants.CONTEXT_USER_KEY).getUid());
        } else {
            createView.setUid(null);
        }
        createView.setIp(routingContext.request().remoteAddress().host());
        return commentService.insert(createView)
                .map(published -> published ? R.okWithMsg("评论成功") : R.okWithMsg("评论成功，请等待审核"));
    }
}
