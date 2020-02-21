package com.jhl.admin.util;

public final class Validator {

    public static void isNotNull(Object o, String errorMsg) {
        if (null == o) throw new NullPointerException(errorMsg);
    }

    public static void isNotNull(Object o) {
        isNotNull(o, "Can not be null");
    }
}
