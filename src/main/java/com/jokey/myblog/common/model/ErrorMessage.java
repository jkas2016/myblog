package com.jokey.myblog.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ErrorMessage {

    @NonNull
    private final String code;
    @NonNull
    private final String message;
    private final Object[] data;

}