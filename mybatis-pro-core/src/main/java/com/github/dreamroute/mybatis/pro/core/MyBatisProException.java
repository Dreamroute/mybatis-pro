package com.github.dreamroute.mybatis.pro.core;

/**
 * @author w.dehai
 */
public class MyBatisProException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -7785085024619152403L;

    public MyBatisProException() {}

    public MyBatisProException(String message) {
        super(message);
    }

    public MyBatisProException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyBatisProException(Throwable cause) {
        super(cause);
    }
}
