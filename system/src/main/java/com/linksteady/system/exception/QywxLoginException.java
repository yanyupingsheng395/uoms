package com.linksteady.system.exception;

/**
 * @author huanhkun
 * @date 2019-11-15
 */
public class QywxLoginException extends Exception {

    private String code;

    private String message;

    public QywxLoginException() {

    }

    public QywxLoginException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public QywxLoginException(String message) {
        super(message);
        this.message = message;
    }
}
