package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 13.03.12
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
class SPSSUtilHolder<T, V> {
    private final T val1;
    private final V val2;
    private int times = 0;
    private long lastAccessTime = System.currentTimeMillis();

    SPSSUtilHolder(T val1, V val2) {
        if (val1 == null || val2 == null) throw new IllegalArgumentException();
        this.val1 = val1;
        this.val2 = val2;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SPSSUtilHolder holder = (SPSSUtilHolder) o;

        if (!val1.equals(holder.val1)) return false;
        return val2.equals(holder.val2);
    }

    boolean isInvalid() {
        return times > 5
                ? System.currentTimeMillis() - lastAccessTime > 60000L
                : System.currentTimeMillis() - lastAccessTime > 1000L;
    }

    void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
        times++;
    }

    public int hashCode() {
        int result;
        result = val1.hashCode();
        result = 31 * result + val2.hashCode();
        return result;
    }
}
