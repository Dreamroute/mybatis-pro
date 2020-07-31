package com.github.dreamroute.mybatis.pro.core;

/**
 * w.dehai
 */
public class SpringDataMyBatisException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -7785085024619152403L;

    public SpringDataMyBatisException() {}

    public SpringDataMyBatisException(String message) {
        super(message);
    }

    public SpringDataMyBatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpringDataMyBatisException(Throwable cause) {
        super(cause);
    }
}
