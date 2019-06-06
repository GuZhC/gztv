package com.dfsx.ganzcms.app.business;

import rx.functions.Action1;

public abstract class AbsAction1<C, T> implements Action1<T> {
    private C constructData;

    public AbsAction1(C constructData) {
        this.constructData = constructData;
    }

    @Override
    public void call(T t) {
        call(constructData, t);
    }

    public abstract void call(C constructData, T data);
}
