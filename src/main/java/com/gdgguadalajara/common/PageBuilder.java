package com.gdgguadalajara.common;

import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.PaginationMeta;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

public class PageBuilder {

    public static <T> PaginatedResponse<T> of(PanacheQuery<T> query, Integer pageIndex, Integer pageSize) {
        var panacheIndex = (pageIndex > 0) ? pageIndex - 1 : 0;
        var paginatedQuery = query.page(panacheIndex, pageSize);
        var list = paginatedQuery.list();
        var totalRecords = paginatedQuery.count();
        var totalPages = (totalRecords == 0) ? 0 : (int) Math.ceil((double) totalRecords / pageSize);
        var nextPage = (pageIndex < totalPages) ? pageIndex + 1 : null;
        var prevPage = (pageIndex > 1) ? pageIndex - 1 : null;
        var meta = new PaginationMeta(
                totalRecords,
                panacheIndex + 1,
                totalPages,
                nextPage,
                prevPage);

        return new PaginatedResponse<>(list, meta);
    }
}
