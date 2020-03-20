package com.jhl.admin.entity;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStat {
    ConcurrentHashMap<String, InternalEntry> cache = new ConcurrentHashMap<>();
    private static final Long _2MINUTES = 2 * 60_000l;

    public void createOrAdd(String host, int count) {
        InternalEntry entry = cache.get(host);
        if (entry != null) {
            entry.setCount(count);
            entry.setLastSetTime(System.currentTimeMillis());
        } else {
            InternalEntry internalEntry = new InternalEntry();
            internalEntry.setCount(count);
            internalEntry.setLastSetTime(System.currentTimeMillis());
            cache.put(host, internalEntry);
        }
    }

    /**
     * 返回2分钟内的全局总数
     * @return
     */
    public Integer getTotal() {
        final long currentTimeMillis = System.currentTimeMillis();
        AtomicInteger total = new AtomicInteger();
        cache.forEachValue(1, internalEntry -> {
            if (currentTimeMillis - internalEntry.getLastSetTime() < _2MINUTES) {
                total.addAndGet(internalEntry.count);
            }
        });
        return total.get();
    }

    @Data
    private class InternalEntry {
        private Integer count;
        private long lastSetTime;

    }


}
