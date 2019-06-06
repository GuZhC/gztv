package com.znq.zbarcode.utils;

import android.graphics.Bitmap;

public class DecodeUtils {

    public static DecodeUtils getInstance() {
        if (instance == null){
            instance = new DecodeUtils();
        }
        return instance;
    }

    public void decodeUrl(String url, ProcessDataTask.DecodeListener decodeListener){
        processDataTask = new ProcessDataTask(url,decodeListener).perform();
    }

    public void decodeBitmap(Bitmap bitmap, ProcessDataTask.DecodeListener decodeListener){

    }

    public void stopDecode(){
        if (processDataTask != null){
            processDataTask.cancelTask();
            processDataTask = null;
        }
    }

    private DecodeUtils(){}

    private static DecodeUtils instance;

    private ProcessDataTask processDataTask;

}
