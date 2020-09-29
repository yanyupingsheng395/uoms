package com.linksteady.operate.exception;
public class PushQywxMessageException extends Exception {

    private String code;

    private String message;

    public PushQywxMessageException() {

    }

    public PushQywxMessageException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public PushQywxMessageException(String message) {
        super(message);
        this.message = message;
    }
}
