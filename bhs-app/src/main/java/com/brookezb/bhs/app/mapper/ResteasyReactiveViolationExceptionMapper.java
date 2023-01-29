package com.brookezb.bhs.app.mapper;

import com.brookezb.bhs.common.model.R;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 将参数校验异常转换为项目通用响应
 *
 * @author brooke_zb
 */
@Provider
public class ResteasyReactiveViolationExceptionMapper implements ExceptionMapper<ResteasyReactiveViolationException> {
    @Override
    public Response toResponse(ResteasyReactiveViolationException e) {
        StringBuilder sb = new StringBuilder();
        for (var violation : e.getConstraintViolations()) {
            sb.append(violation.getMessage()).append("\n");
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(R.fail(sb.toString().trim())).build();
    }
}
