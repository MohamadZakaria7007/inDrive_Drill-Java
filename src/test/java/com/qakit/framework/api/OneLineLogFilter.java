package com.qakit.framework.api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** "POST https://.../booking -> 200 (312 ms)": enough to follow a run without drowning the log. */
public final class OneLineLogFilter implements Filter {

    private static final System.Logger LOG = System.getLogger(OneLineLogFilter.class.getName());

    @Override
    public Response filter(
            FilterableRequestSpecification request, FilterableResponseSpecification response,
            FilterContext context) {
        long start = System.nanoTime();
        Response result = context.next(request, response);
        LOG.log(System.Logger.Level.INFO, "{0} {1} -> {2} ({3} ms)", request.getMethod(),
                request.getURI(), result.statusCode(), (System.nanoTime() - start) / 1_000_000);
        return result;
    }
}
