package com.dfsx.core.network;

import com.dfsx.core.exception.ApiException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by liuwb on 2016/7/13.
 */
public class HttpUtils {

    public static void doPost(final String urlString,
                              final JSONObject jsonParams,
                              final String token,
                              final IHttpResponseListener listener) {

        Observable.just(null).
                subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Object o) {
                        JSONObject jsonObject = JsonHelper.httpPostJson(urlString, jsonParams, token);
                        return Observable.just(jsonObject);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (listener != null) {
                            listener.onError(null, new ApiException(throwable));
                        }
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        if (listener != null) {
                            listener.onComplete(null, jsonObject.toString());
                        }
                    }
                });
    }

    public static void doGet(final String urlString, final String token,
                             final IHttpResponseListener listener) {
        Observable.just(null).
                subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Object o) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = JsonHelper.httpGetJson(urlString, token);
                        } catch (ApiException e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onError(null, e);
                            }
                        }
                        return Observable.just(jsonObject);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (listener != null) {
                            listener.onError(null, new ApiException(throwable));
                        }
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        if (listener != null && jsonObject != null) {
                            listener.onComplete(null, jsonObject.toString());
                        }
                    }
                });
    }

    public static void doPut(final String urlString, final JSONObject jsonParams, final String token, final IHttpResponseListener listener) {
        Observable.just(null).
                subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Object o) {
                        JSONObject jsonObject = JsonHelper.httpPutJson(urlString, jsonParams, token);
                        return Observable.just(jsonObject);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (listener != null) {
                            listener.onError(null, new ApiException(throwable));
                        }
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        if (listener != null) {
                            listener.onComplete(null, jsonObject.toString());
                        }
                    }
                });
    }

    public static void doDel(final String urlString, final String token, final IHttpResponseListener listener) {
        Observable.just(null).
                subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(Object o) {
                        JSONObject jsonObject = JsonHelper.httpDelJson(urlString, token);
                        return Observable.just(jsonObject);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (listener != null) {
                            listener.onError(null, new ApiException(throwable));
                        }
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        if (listener != null) {
                            listener.onComplete(null, jsonObject.toString());
                        }
                    }
                });
    }
}
