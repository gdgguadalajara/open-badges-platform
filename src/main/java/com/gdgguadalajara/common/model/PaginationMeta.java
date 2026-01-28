package com.gdgguadalajara.common.model;

public record PaginationMeta(
        Long totalRecords,
        Integer currentPage,
        Integer totalPages,
        Integer nextPage,
        Integer prevPage) {
}
