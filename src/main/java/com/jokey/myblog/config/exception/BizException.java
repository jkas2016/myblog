package com.jokey.myblog.config.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final ExceptionCode code;
    private final String detailMessage;

    public BizException( ExceptionCode code, String detailMessage ) {
        super( detailMessage );
        this.code = code;
        this.detailMessage = detailMessage;
    }

}