package com.dfsx.core.network;


import com.dfsx.core.exception.ApiException;

public interface IHttpResponseListener {
    /**
     * get response from services 
     * @param response
     */
    void onComplete(Object tag, String response);

    void onError(Object tag, ApiException e);

}
