package com.himynameisilnano.account.boundary;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Objects;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        int code = e.getResponse().getStatus();

        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("exception_type", e.getClass().getName())
                .add("code", code)
                .add("error", Objects.nonNull(e.getMessage()) ? e.getMessage() : null);

        return Response.status(code)
                .entity(builder.build())
                .build();
    }
}
