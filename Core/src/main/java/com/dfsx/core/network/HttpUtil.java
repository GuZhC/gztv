package com.dfsx.core.network;

import android.content.Context;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressRequestListener;

import java.io.File;
import java.io.IOException;

/**
 * http请求工具类
 *
 * @author yanyi
 */
public class HttpUtil {

    private static AbsHttp ihttp = OkHttpUtil.getInstance();

    public static void doPost(String httpUrl,
                              IHttpParameters params, String token, IHttpResponseListener requestListener) {
        ihttp.doPost(httpUrl, params, token, requestListener);
    }

    public static void doPut(String httpUrl,
                             IHttpParameters params, String token, IHttpResponseListener requestListener) {
        ihttp.doPut(httpUrl, params, token, requestListener);
    }

    public static void doDel(String httpUrl,
                             IHttpParameters params, String token, IHttpResponseListener requestListener) {
        ihttp.doDel(httpUrl, params, token, requestListener);
    }

    public static void doGet(String httpUrl, String token,
                             IHttpResponseListener requestListener) {
        ihttp.doGet(httpUrl, token, requestListener);
    }

    public static void doGet(String httpUrl, String token,
                             HttpParameters params, IHttpResponseListener requestListener) {
        ihttp.doGet(httpUrl, params, token, requestListener);
    }

    public static void cancel(Object object) {
        ihttp.cancel(object);
    }

    public static void cancel(Context context) {
        if (context != null) {
            ihttp.cancel(context.getClass().getName());
        }
    }

    /**
     * 同步上传文件,可带文件上传进度监听
     *
     * @param httpUrl
     * @param file
     * @param uiProgressRequestListener 回调参数依次为 long bytesWrite, long contentLength, boolean done
     * @return
     */
    public static String uploadFileSynchronized(String httpUrl, File file,
                                                UIProgressRequestListener uiProgressRequestListener) throws IOException {
        return ihttp.uploadSync(httpUrl, file, uiProgressRequestListener);
    }

    /**
     * 同步上传文件,可带文件上传进度监听
     *
     * @param httpUrl
     * @param filePath
     * @param uiProgressRequestListener 回调参数依次为 long bytesWrite, long contentLength, boolean done
     * @return
     * @throws IOException
     */
    public static String uploadFileSynchronized(String httpUrl, String filePath,
                                                UIProgressRequestListener uiProgressRequestListener) throws IOException {
        return ihttp.uploadSync(httpUrl, filePath, uiProgressRequestListener);
    }

    /**
     * 异步上传文件,可带文件上传进度监听
     *
     * @param httpUrl
     * @param file
     * @param listener
     * @param uiProgressRequestListener 回调参数依次为 long bytesWrite, long contentLength, boolean done
     * @throws IOException
     */
    public static void uploadFileAsynchronous(String httpUrl, File file, IHttpResponseListener listener,
                                              UIProgressRequestListener uiProgressRequestListener) throws IOException {
        ihttp.uploadAsync(httpUrl, file, listener, uiProgressRequestListener);
    }

    /**
     * 异步上传文件,可带文件上传进度监听
     *
     * @param httpUrl
     * @param filePath
     * @param listener
     * @param uiProgressRequestListener 回调参数依次为 long bytesWrite, long contentLength, boolean done
     * @throws IOException
     */
    public static void uploadFileAsynchronous(String httpUrl, String filePath, IHttpResponseListener listener,
                                              UIProgressRequestListener uiProgressRequestListener) throws IOException {
        ihttp.uploadAsync(httpUrl, filePath, listener, uiProgressRequestListener);
    }

    /**
     * 没有包装自线程的执行方法
     *
     * @param httpUrl
     * @param httpParameters
     * @return
     */
    public static String execute(String httpUrl, IHttpParameters httpParameters, String token) {
        return ihttp.execute(httpUrl, httpParameters, token);
    }

    public static String executeGet(String httpUrl, IHttpParameters httpParameters, String token) {
        return ihttp.executeGet(httpUrl, httpParameters, token);
    }

    /**
     * * by  create heyang 2018/1/19
     * 增加同步post请求
     * @param httpUrl
     * @param params
     * @param token
     * @return
     */
    public static String exePost(String httpUrl, IHttpParameters params, String token) {
           return   ihttp.exePost(httpUrl, params, token);
    }


}
