package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.ImageUtil;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.util.UtilHelp;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;

/**
 * Created by liuwb on 2016/11/9.
 */
public class LiveFileUploadHelper {

    private Context context;
    private static final String GET_UPLOAD_URL = "/public/covers/uploader";
    private String compressImagePath;
    private String compressImageDir;

    public LiveFileUploadHelper(Context context) {
        this.context = context;
        compressImageDir = context.getCacheDir().getPath() + "/dfsx/";
        compressImagePath = compressImageDir + "upload_test.png";
    }

    public void uploadFile(String filePath, Action1<String> callback) {
        Observable.just(filePath)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {//压缩图片
                        Bitmap bitmap = ImageUtil.getSmallBitmap(s, 800, 800);
                        if (bitmap != null) {
                            File saveFile = new File(compressImagePath);
                            if (saveFile.exists()) {
                                boolean isOk = saveFile.delete();
                                if (!isOk) {
                                    compressImagePath = compressImageDir + System.currentTimeMillis() + ".png";
                                }
                            }
                            ImageUtil.saveBitmapToFile(bitmap, compressImagePath);
                            return Observable.just(compressImagePath);
                        }
                        return Observable.just(null);
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String path) {
                        if (TextUtils.isEmpty(path)) {
                            return null;
                        }
                        String serverlUrl = getCoversUploadUrl();
                        if (TextUtils.isEmpty(serverlUrl)) {
                            return null;
                        }
                        serverlUrl = UtilHelp.checkUrl(serverlUrl);
                        String response = null;
                        try {
                            JSONObject res = JsonHelper.httpUpload(serverlUrl, path, null);
                            response = res.toString();
                            //删除临时文件
                            File saveFile = new File(compressImagePath);
                            if (saveFile.exists()) {
                                boolean isOk = saveFile.delete();
                                Log.e("TAG", "isOk === " + isOk);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return response;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    private String getCoversUploadUrl() {
        String result = "";
        String url = App.getInstance().getmSession().getLiveServerUrl() + GET_UPLOAD_URL;
        result = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        if (result != null && !TextUtils.isEmpty(result)) {
            try {
                JSONObject obj = JsonCreater.jsonParseString(result);
                if (obj != null) {
                    String token = UtilHelp.checkTokenError(obj);
                    if (token != null)
                        result = HttpUtil.executeGet(url, new HttpParameters(), token);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
