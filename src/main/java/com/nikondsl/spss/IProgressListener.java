package com.nikondsl.spss;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 16/1/2008
 * Time: 10:29:07
 * To change this template use File | Settings | File Templates.
 */
public interface IProgressListener {
    void nextStep();

    void finish();
}
