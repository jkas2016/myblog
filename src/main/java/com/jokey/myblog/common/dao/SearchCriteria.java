package com.jokey.myblog.common.dao;

import lombok.Data;

import java.util.List;

@Data
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

    private String key;
    private T value;
    private List<T> list;
    private SearchOperation operation;

    public SearchCriteria(String key, T value, SearchOperation operation){
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public SearchCriteria(String key, List<T> list, SearchOperation operation){
        this.key = key;
        this.list = list;
        this.operation = operation;
    }

}