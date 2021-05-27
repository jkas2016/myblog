package com.jokey.myblog.common.dao;

import lombok.*;

import java.util.List;

/**
 * Where 조건 K, V
 *
 * @param <T>
 */
@Builder
@Getter
@ToString
public class SearchCriteria<T> {

    public enum SearchOperation {
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_EQUAL,
        LESS_THAN_EQUAL,
        NOT_EQUAL,
        EQUAL,
        MATCH,
        MATCH_START,
        MATCH_END,
        IN,
        NOT_IN
    }

    @NonNull
    private final String key;
    private final T value;
    private final List<T> list;
    @NonNull
    private final SearchOperation operation;

}