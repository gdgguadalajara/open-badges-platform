package com.gdgguadalajara.common.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

public class PaginationRequestParams {
    @QueryParam("page")
    @DefaultValue("1")
    @Positive
    public Integer page;

    @QueryParam("size")
    @DefaultValue("10")
    @Positive
    @Max(100)
    public Integer size;
}
