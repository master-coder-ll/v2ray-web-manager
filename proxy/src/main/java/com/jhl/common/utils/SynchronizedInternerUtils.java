package com.jhl.common.utils;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class SynchronizedInternerUtils {

    private final static Interner< Object> STRING_WEAK_POLL = Interners.newWeakInterner();



    public  static <T> T  getWeakReference(T t){
       return (T) STRING_WEAK_POLL.intern(t);
    }
}
