package com.linksteady.operate.exception;

/**
 * @author huanhkun
 * @date 2019-11-15
 */
public class OptimisticLockException extends Exception {

    private String code;

    private String message;

    public OptimisticLockException() {

    }

    public OptimisticLockException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public OptimisticLockException(String message) {
        super(message);
        this.message = message;
    }
}
