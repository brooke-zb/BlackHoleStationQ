package com.brookezb.bhs.service.exception;

/**
 * @author brooke_zb
 */
public class ServiceQueryException extends RuntimeException {
    public ServiceQueryException(String message) {
        super(message, null, false, false);
    }

    public ServiceQueryException(String message, Throwable cause) {
        super(message, cause, false, false);
    }
}
