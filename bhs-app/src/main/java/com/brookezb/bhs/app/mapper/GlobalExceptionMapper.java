package com.brookezb.bhs.app.mapper;

import com.brookezb.bhs.common.model.R;
import com.brookezb.bhs.service.exception.ServiceQueryException;
import io.quarkus.logging.Log;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author brooke_zb
 */
public class GlobalExceptionMapper {
    // 404
    @ServerExceptionMapper
    public Uni<Response> notFound(NotFoundException e) {
        Log.error(e.getMessage(), e);
        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).entity(R.fail(e.getMessage())).build());
    }

    // 400
    @ServerExceptionMapper({BadRequestException.class, ServiceQueryException.class})
    public Uni<Response> badRequest(Exception e) {
        Log.error(e.getMessage(), e);
        return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity(R.fail(e.getMessage())).build());
    }

    // 401
    @ServerExceptionMapper
    public Uni<Response> unauthorized(UnauthorizedException e) {
        Log.error(e.getMessage(), e);
        return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).entity(R.fail(e.getMessage())).build());
    }

    // 403
    @ServerExceptionMapper
    public Uni<Response> forbidden(ForbiddenException e) {
        Log.error(e.getMessage(), e);
        return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).entity(R.fail(e.getMessage())).build());
    }

    @ServerExceptionMapper
    public Uni<Response> webException(WebApplicationException e) {
        Log.error(e.getMessage(), e);
        return Uni.createFrom().item(Response.status(e.getResponse().getStatus()).entity(R.fail(e.getMessage())).build());
    }
}
