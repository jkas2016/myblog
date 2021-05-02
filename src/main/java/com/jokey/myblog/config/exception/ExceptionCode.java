package com.jokey.myblog.config.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    INTERNAL_SERVER_ERROR( "EC500" );

    private final String code;

    ExceptionCode( String code ) {
        this.code = code;
    }

}
