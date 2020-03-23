package com.jhl.admin.entity;

import com.jhl.admin.cache.ConnectionStatCache;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionStat {
    ConcurrentHashMap<String, InternalEntry> cache = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private volatile Long lastBlock=0L;
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
     *
     * @return
     */
    public Integer getTotal() {
        final long currentTimeMillis = System.currentTimeMillis();
        AtomicInteger total = new AtomicInteger();
        List<String> removeKeys = new ArrayList<>();
        cache.forEach((key, value) -> {
            if (currentTimeMillis - value.getLastSetTime() < ConnectionStatCache.EXPIRE_TIME) {
                total.addAndGet(value.count);
            } else {
                removeKeys.add(key);
            }
        });
        removeKeys.forEach(key -> cache.remove(key));
        int count = total.get();
        return count;
    }

    @Data
    private class InternalEntry {
        private Integer count;
        private long lastSetTime;

    }


}
