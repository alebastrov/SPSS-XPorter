package com.nikondsl.spss;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 16/1/2008
 * Time: 10:30:53
 * To change this template use File | Settings | File Templates.
 */
public interface IBufferProgressListener {
    void status(int vacated, int free);

    void finish();
}
