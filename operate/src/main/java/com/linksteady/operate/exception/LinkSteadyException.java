package com.linksteady.operate.exception;

/**
 * @author hxcao
 * @date 2019-11-15
 */
public class LinkSteadyException extends Exception {

    private String code;

    private String message;

    public LinkSteadyException() {

    }

    public LinkSteadyException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public LinkSteadyException(String message) {
        super(message);
        this.message = message;
    }
}
