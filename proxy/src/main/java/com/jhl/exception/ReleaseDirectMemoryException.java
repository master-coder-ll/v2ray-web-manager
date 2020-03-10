package com.jhl.exception;

/**
 * 申明需要内存回收
 *
 */
public class ReleaseDirectMemoryException extends  Exception {
private static  String str ="此异常忽略，声明需要回收直接内存|";
    public ReleaseDirectMemoryException() {
        this(str);
    }

    public ReleaseDirectMemoryException(String message) {
        super(str+message);
    }

    public ReleaseDirectMemoryException(String message, Throwable cause) {
        super(str+message, cause);
    }

    public ReleaseDirectMemoryException(Throwable cause) {
        this(str,cause);
    }

    public ReleaseDirectMemoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(str+message, cause, enableSuppression, writableStackTrace);
    }
}
