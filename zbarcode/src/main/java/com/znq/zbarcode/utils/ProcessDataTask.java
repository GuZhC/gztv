package com.znq.zbarcode.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.dtr.zbar.build.ZBarDecoder;


public class ProcessDataTask extends AsyncTask<Void,Void,String> {

    private Bitmap bitmap;
    private String url;
    private DecodeListener decodeListener;

    public ProcessDataTask(String url,DecodeListener decodeListener){
        this.url = url;
        this.decodeListener = decodeListener;
    }

    public ProcessDataTask(Bitmap bitmap,DecodeListener decodeListener){
        this.bitmap = bitmap;
        this.decodeListener = decodeListener;
    }

    ProcessDataTask perform() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return this;
    }

    void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected String doInBackground(Void... strings) {
        if (!TextUtils.isEmpty(url)){
            bitmap = BitmapUtils.getBitmapFromUrl(url);
        }
        if (bitmap != null){
            byte []array = BitmapUtils.getNV21(bitmap.getWidth(),bitmap.getHeight(),bitmap);
            return new ZBarDecoder().decodeRaw(array,bitmap.getWidth(),bitmap.getHeight());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        clearBitmap();
        decodeListener.decodeResult(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        clearBitmap();
    }

    public interface DecodeListener{
        void decodeResult(String result);
    }

    private void clearBitmap(){
        bitmap.recycle();
        bitmap = null;
    }

}
