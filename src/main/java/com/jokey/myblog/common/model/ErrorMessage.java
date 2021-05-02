package com.jokey.myblog.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

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