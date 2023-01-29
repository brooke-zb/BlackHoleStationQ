package com.brookezb.bhs.app.filter;

import io.quarkus.logging.Log;
import io.vertx.core.http.HttpServerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author brooke_zb
 */
@Provider
public class APILoggingFilter implements ContainerRequestFilter {
    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Log.debugf("%s %s from %s", request.method(), request.path(), request.remoteAddress());
    }
}
