package com.jokey.myblog.config.utils;

/**
 * HttpServletRequest parsing log template
 */
public enum Constants {

    KEY_MESSAGE_ID( "__message_id" ),
    KEY_LOGIN( "__login" ),
    KEY_EXCEPTION( "__exception" ),

    HEADER_MANUFACTURER( "manufacturer" ),
    HEADER_MODEL( "model" ),
    HEADER_OS_TYPE( "os_type" ),
    HEADER_OS_VER( "os_ver" ),
    HEADER_APP_ID( "app_id" ),
    HEADER_APP_VER( "app_ver" ),
    HEADER_COUNTRY( "country" ),
    HEADER_LANG( "lang" ),
    HEADER_NONCE( "nonce" );

    private final String message;

    Constants( String message ) {
        this.message = message;
    }

    public String toString() {
        return this.message;
    }
}