package com.linksteady.operate.exception;

/**
 * @author huanhkun
 * @date 2019-11-15
 */
public class SendCouponException extends Exception {

    private String code;

    private String message;

    public SendCouponException() {

    }

    public SendCouponException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SendCouponException(String message) {
        super(message);
        this.message = message;
    }
}
