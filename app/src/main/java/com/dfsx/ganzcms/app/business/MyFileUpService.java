package com.dfsx.ganzcms.app.business;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.ProgessCallbacface;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.UploadAttment;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.selectedmedia.MediaModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * create  by   heyang  2017/7/20
 */

public class MyFileUpService extends Service {
    private static final String TAG = "MyFileUpService";

    private static final String pictureUrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/pictures/uploader";
    private long cId;
    private String mTitle;
    private String mContent;
    private ArrayList<MediaModel> attments;
    private List<String> tagsList;
    private int total;
    private int currentPos;
    private int type;  // 0; 图片  1:视频

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate... thread id:" + Thread.currentThread().getId());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //接收从Activity发来的数据
//  //        String url = intent.getStringExtra("url");
        if (intent != null) {
            UploadAttment<ArrayList<String>> entry = (UploadAttment<ArrayList<String>>) intent.getSerializableExtra("param");
            attments = PictureManager.getInstance().getmDllist();
            if (entry != null) {
                cId = entry.getId();
                mTitle = entry.getTitle();
                mContent = entry.getContent();
                tagsList = entry.getParam();
                type = entry.getType();
                //启动下载任务
//        new DownLoadTask().execute(url);
                App.getInstance().setUpFileFlag(true);
                if (attments != null) total = attments.size();
                doUpFile();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /***
     private class DownLoadTask extends AsyncTask<String, Void, String> {
    @Override protected String doInBackground(String... params) {
    for (int i = 1; i <= 10; i++) {
    //模拟下载发送进度
    Intent intent = new Intent();
    intent.setAction(IntentUtil.ACTION_UPFILE_PROGRESS_MSG);
    intent.putExtra("progress", 10 * i);
    //休息一秒
    SystemClock.sleep(1000);
    //发送广播
    sendBroadcast(intent);
    }
    return null;
    }
    }   **/

    /**
     * 上传
     *
     * @param
     */
    public void doUpFile() {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/columns/" + cId + "/threads";
        Observable.just(url).
                observeOn(Schedulers.io()).
                map(new Func1<String, Long>() {
                    @Override
                    public Long call(String url) {
                        long topicId = -1;
                        String upFIleUrl = getImaUpfileUrl();
                        if (type == 1) {
                            upFIleUrl = getVideoUpUrl();
                        }
                        if (upFIleUrl == null || TextUtils.isEmpty(upFIleUrl)) {
                            sendMsg(-1, "获取上传地址失败!");
                        }
                        JSONArray arr = new JSONArray();
                        if (attments != null && !attments.isEmpty()) {
                            for (int i = 0; i < attments.size(); i++) {
                                currentPos = i + 1;
                                MediaModel model = attments.get(i);
                                String ralatPath = "";
                                String upUrl = UtilHelp.checkUrl(upFIleUrl);
                                JSONObject json = null;
                                try {
                                    json = JsonHelper.httpUpload(upUrl, model.url, new ProgessCallbacface() {
                                        @Override
                                        public boolean MyObtainProgressValues(int values) {
                                            //休息一秒
//                                            SystemClock.sleep(100);
                                            sendMsg(values);
                                            return false;
                                        }
                                    });
                                    if (!App.getInstance().isUpFileFlag()) break;
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    sendMsg(-1, e.toString());
                                }
                                if (json != null) {
//                                    sendMsg(92, "");
                                    int isOk = json.optInt("isOK");
                                    if (isOk == 1) {
                                        ralatPath = json.optString("relativepath");
                                        ralatPath += json.optString("name");
                                    }
                                }
                                if (!TextUtils.isEmpty(ralatPath)) {
//                                    sendMsg(94, "");
                                    try {
                                        JSONObject ob = new JSONObject();
                                        JSONArray yy = new JSONArray();
                                        yy.put(ralatPath);
                                        ob.put("name", model.name);
                                        ob.put("paths", yy);
                                        arr.put(ob);
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
//                                        sendMsg(-1, e1.toString());
                                    }
//                                    sendMsg(96, "");
                                }
                            }
                        }
                        JSONObject parmaObj = new JSONObject();
                        JSONArray tagsArr = new JSONArray();
                        if (tagsList != null && tagsList.size() > 0) {
                            for (String str : tagsList) {
                                tagsArr.put(str);
                            }
                        }
                        try {
//                            sendMsg(98, "");
                            parmaObj.put("title", mTitle);
                            parmaObj.put("content", mContent);
                            parmaObj.put("attachment_infos", arr);
                            parmaObj.put("tags", tagsArr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sendMsg(-1, e.toString());
                        }
                        if (App.getInstance().isUpFileFlag()) {
                            JSONObject result = JsonHelper.httpPostJson(url, parmaObj, App.getInstance().getCurrentToken());
                            if (result != null && !TextUtils.isEmpty(result.toString())) {
                                topicId = checkUserVerityError(result);
                                if (topicId != -2)
                                    topicId = result.optLong("id");
                                if (topicId == -1 || topicId == 0) {
                                    sendMsg(-1, "发表主题失败" + result.optString("message"));
                                } else if (topicId == -2) {
                                    sendMsg(-1, "用户未验证");
                                } else
                                    sendMsg(100, "");
                            }
                        }
                        return topicId;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long flag) {
                    }
                });
    }

    public String getImaUpfileUrl() {
        String result = "";
        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), App.getInstance().getCurrentToken());
        if (result != null && !TextUtils.isEmpty(result)) {
            try {
                JSONObject obj = JsonCreater.jsonParseString(result);
                if (obj != null) {
                    String token = UtilHelp.checkTokenError(obj);
                    if (token != null)
                        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), token);
                }
            } catch (ApiException e) {
                e.printStackTrace();
                sendMsg(-1, e.toString());
            }
        }
        return result;
    }

    public String getVideoUpUrl() {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/attachments/uploader";
        String result = "";
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

    /**
     * 检查 用户是否验证
     */
    public long checkUserVerityError(JSONObject obj) {
        long id = 0;
        if (obj != null) {
            id = obj.optLong("code");
            if (id == 1100) {
                id = -2;
            }
        }
        return id;
    }

    public void sendMsg(int fval) {
        sendMsg(fval, "");
    }

    public void sendMsg(int fval, String errormsg) {
        Intent intent = new Intent();
        intent.setAction(IntentUtil.ACTION_UPFILE_PROGRESS_MSG);
//        if (fval != -1 && fval != 0 && fval != 100) {
        if (fval != -1) {
//            currentPos++;
            currentPos = currentPos > total ? total : currentPos;
            errormsg = currentPos + "/" + total;
        }
        intent.putExtra("progress", fval);
        intent.putExtra("errmsg", errormsg);
        //休息一秒
//        SystemClock.sleep(1000);
        //发送广播
        if (App.getInstance().isUpFileFlag())
            sendBroadcast(intent);
    }


}
