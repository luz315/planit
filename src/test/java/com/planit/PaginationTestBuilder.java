package com.planit;

import com.planit.domain.model.Pagination;

import java.util.List;

public class PaginationTestBuilder<T> {

    private List<T> contents;
    private int pageNumber = 0;
    private int pageSize = 10;
    private long totalElements = 0;

    public static <T> PaginationTestBuilder<T> builder() {
        return new PaginationTestBuilder<>();
    }

    public PaginationTestBuilder<T> contents(List<T> contents) {
        this.contents = contents;
        this.totalElements = contents.size(); // 기본 totalElements 설정
        return this;
    }

    public PaginationTestBuilder<T> pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public PaginationTestBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PaginationTestBuilder<T> totalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    public Pagination<T> build() {
        return Pagination.of(contents, pageNumber, pageSize, totalElements);
    }
}
