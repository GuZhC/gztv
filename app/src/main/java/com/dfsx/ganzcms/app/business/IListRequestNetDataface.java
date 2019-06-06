package com.dfsx.ganzcms.app.business;

import com.dfsx.core.exception.ApiException;

/**
 *
 * heyang  2017-9-8   Rx 请求网络数据返回
 * @param <T>
 */


public interface IListRequestNetDataface<T> {
    public void onSucces(T t);

    public void onFail(ApiException e);
}
