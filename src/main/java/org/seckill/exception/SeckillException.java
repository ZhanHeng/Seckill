package org.seckill.exception;

/**
 * 异常父类:秒杀异常，继承RuntimeException
 * Created by lenovo on 2016/11/9.
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
