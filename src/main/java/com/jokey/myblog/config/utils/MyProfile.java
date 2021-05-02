package com.jokey.myblog.config.utils;

/**
 * gradle profile
 */
public enum MyProfile {
    local,
    dev,
    prod;

    public String toString() {
        return this.name();
    }
}
