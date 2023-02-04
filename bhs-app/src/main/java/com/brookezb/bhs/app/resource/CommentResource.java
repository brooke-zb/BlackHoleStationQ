package com.brookezb.bhs.app.resource;

import com.brookezb.bhs.common.dto.CommentView;
import com.brookezb.bhs.common.model.PageInfo;
import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.CommentService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
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
    @Path("")
    public Uni<R<PageInfo<CommentView>>> findPageByArticleId(@RestQuery @DefaultValue("1") @Min(value = 1, message = "页数不能小于1") int page, @RestQuery long aid) {
        return commentService.findAllByArticleId(aid, page)
                .map(R::ok);
    }
}
