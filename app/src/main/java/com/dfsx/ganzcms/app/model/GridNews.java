package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

public abstract class GridNews<T> implements Serializable, INewsGridData {

    protected T data;

    public GridNews(T data) {
        this.data = data;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
