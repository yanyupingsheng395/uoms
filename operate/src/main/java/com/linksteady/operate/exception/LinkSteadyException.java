package com.linksteady.operate.exception;

/**
 * @author hxcao
 * @date 2019-11-15
 */
public class LinkSteadyException extends Throwable {

    private String code;

    private String message;

    public LinkSteadyException() {

    }

    public LinkSteadyException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public LinkSteadyException(String message) {
        this.message = message;
    }
}
