package com.dfsx.ganzcms.app.business;

/**
 * 执行单个对象的线程
 *
 * @param <T>
 */
public abstract class AbsRunnable<T> implements Runnable {

    private T data;

    public AbsRunnable(T data) {
        this.data = data;
    }

    @Override
    public void run() {
        runData(data);
    }

    public abstract void runData(T data);

}
