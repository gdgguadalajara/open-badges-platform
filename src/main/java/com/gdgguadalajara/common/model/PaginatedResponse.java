package com.gdgguadalajara.common.model;

import java.util.List;

public class PaginatedResponse<T> {
    public List<T> data;
    public PaginationMeta meta;

    public PaginatedResponse(List<T> data, PaginationMeta meta) {
        this.data = data;
        this.meta = meta;
    }
}