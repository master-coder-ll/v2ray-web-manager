package com.jhl.utils;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;


public class Utils {

     private   static Interner<String> STRING_WEAK_POLL = Interners.newWeakInterner();

     public static String getStringWeakReference(String key){
         return STRING_WEAK_POLL.intern(key);
     }
}
