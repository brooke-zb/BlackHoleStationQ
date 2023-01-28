package com.brookezb.bhs.service.exception;

/**
 * @author brooke_zb
 */
public class ServiceQueryException extends RuntimeException {
    public ServiceQueryException(String message) {
        super(message);
    }

    public ServiceQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
