package com.nikondsl.spss.record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: May 5, 2010
 * Time: 4:52:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnmodifiableDate {
    static Map<Long, UnmodifiableDate> cache = new ConcurrentHashMap<Long, UnmodifiableDate>();
    private final long timeInMillis;

    private UnmodifiableDate(long timeInMillis) {
        this.timeInMillis = timeInMillis / 1000 * 1000;
    }

    public static UnmodifiableDate getInstance(long timeInMillis) {
        long key = timeInMillis / 1000 * 1000;
        UnmodifiableDate result = cache.get(key);
        if (result != null) return result;
        result = new UnmodifiableDate(key);
        if (cache.size() > 1000) return result;
        cache.put(key, result);
        return result;
    }

    public long getTime() {
        return timeInMillis;
    }
}
