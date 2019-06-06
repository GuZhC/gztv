package com.dfsx.ganzcms.app.business;

import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.file.upload.OkHttpUploadFile;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.core.file.upload.UploadFileData;
import com.dfsx.ganzcms.app.model.WordData;
import com.dfsx.logonproject.busniness.TokenHelper;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 队列投稿
 */
public class PostWordManager {

    private static PostWordManager instance = new PostWordManager();
    private OkHttpUploadFile uploadFileHelper;

    private Observer<WordData> postWordObserver;

    private BlockingQueue<WordData> postWordDataQueue = new LinkedBlockingQueue<>();

    private PostWordManager() {
        uploadFileHelper = new OkHttpUploadFile();
    }

    /**
     * 是否正在发布
     */
    private boolean isPosting = false;

    public static PostWordManager getInstance() {
        return instance;
    }

    public void setUploadPercentListener(OkHttpUploadFile.UploadPercentListener uploadPercentListener) {
        if (uploadFileHelper != null) {
            uploadFileHelper.setUploadPercentListener(uploadPercentListener);
        }
    }

    public void post(WordData word) {
        postWordDataQueue.add(word);
        startPost();
    }

    private void startPost() {
        if (!isPosting) {
            new PostThread().start();
        }
    }

    class PostThread extends Thread {
        @Override
        public void run() {
            super.run();
            WordData word;
            isPosting = true;
            Log.e("TAG", "thread start----");
            while ((word = postWordDataQueue.poll()) != null) {
                Log.e("TAG", "word post ----" + word.getTitle());
                Observable<WordData> dataObservable = postWordInThread(word);
                if (postWordObserver != null) {
                    dataObservable.observeOn(AndroidSchedulers.mainThread())
                            .subscribe(postWordObserver);
                } else {
                    dataObservable.subscribe();
                }
            }
            isPosting = false;
        }
    }

    private Observable<WordData> postWordInThread(WordData word) {
        if (word != null && !word.isPostSuccess()) {
            ArrayList<UploadFileData> list = word.getUploadFileDataList();
            String videoContent = "";
            String imageContent = "";
            if (list != null) {
                boolean is = uploadFileHelper.upload(null,list);
                Log.e("TAG", "file upload result == " + is);
                if (is) {
                    ArrayList<String> videoUploadList = new ArrayList<>();
                    ArrayList<String> imageUploadedList = new ArrayList<>();
                    for (UploadFileData data1 : list) {
                        if (TextUtils.equals(data1.getFileType(), UploadFileData.TYPE_VIDEO)) {
                            if (!TextUtils.isEmpty(data1.getResult().getServicePath())) {
                                videoUploadList.add(data1.getResult().getServicePath());
                            }
                        } else {
                            if (!TextUtils.isEmpty(data1.getResult().getServicePath())) {
                                imageUploadedList.add(data1.getResult().getServicePath());
                            }
                        }
                    }
                    if (!videoUploadList.isEmpty()) {
                        ArrayList<Long> videoIds = postVMSVideoList(word.getTitle(),
                                videoUploadList.toArray(new String[0]));
                        if (videoIds == null) {
                            Log.e("TAG", "video vms error----");
                            word.setErrorProgressType(WordData.ERROR_POST_VMS_VIDEO);
                            return Observable.just(word);
                        } else {
                            StringBuffer sb = new StringBuffer();
                            for (Long id : videoIds) {
                                String videoString = "<!--VIDEO#" + id + ",0,0-->";
                                sb.append(videoString);
                            }
                            videoContent = sb.toString();
                        }
                    }
                    if (!imageUploadedList.isEmpty()) {
                        StringBuffer imageBuffer = new StringBuffer();
                        for (String img : imageUploadedList) {
                            imageBuffer.append("<!--PICTURE#" + img + ",0,0-->");
                        }
                        imageContent = imageBuffer.toString();
                    }
                } else {
                    word.setErrorProgressType(WordData.ERROR_UPLOAD_FILE);
                    return Observable.just(word);
                }
            }
            String content = word.getContent() + imageContent + videoContent;
            word.setTextAndFileContent(content);
            long postId = postWord(word, content);
            if (postId <= 0) {
                word.setErrorProgressType(WordData.ERROR_POST_WORD_DATA);
                return Observable.just(word);
            }
        }
        word.setErrorProgressType(WordData.ERROR_NONE);
        word.setPostSuccess(true);
        return Observable.just(word);
    }


    /**
     * 重试功能
     */
    public void repost(WordData word) {
        if (word != null && !word.isPostSuccess()) {
            if (word.getErrorProgressType() == WordData.ERROR_POST_WORD_DATA) {
                Observable<WordData> postLast = Observable.just(word)
                        .observeOn(Schedulers.io())
                        .map(new Func1<WordData, WordData>() {
                            @Override
                            public WordData call(WordData wordData) {
                                long postId = postWord(wordData, wordData.getTextAndFileContent());
                                if (postId <= 0) {
                                    wordData.setErrorProgressType(WordData.ERROR_POST_WORD_DATA);
                                    wordData.setPostSuccess(false);
                                } else {
                                    wordData.setErrorProgressType(WordData.ERROR_NONE);
                                    wordData.setPostSuccess(true);
                                }
                                return wordData;
                            }
                        });
                if (postWordObserver != null) {
                    postLast.observeOn(AndroidSchedulers.mainThread())
                            .subscribe(postWordObserver);
                } else {
                    postLast.subscribe();
                }

            } else {
                post(word);
            }
        }
    }

    private ArrayList<Long> postVMSVideoList(String title, String... videoPaths) {
        String url = App.getInstance()
                .getmSession().getContentcmsServerUrl() + "/public/users/current/contribute/videos";
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            JSONArray videoArray = new JSONArray();
            json.put("videos", videoArray);

            if (videoPaths != null) {
                for (String path : videoPaths) {
                    JSONObject item = new JSONObject();
                    JSONArray pathArray = new JSONArray();
                    pathArray.put(path);
                    item.put("paths", pathArray);
                    videoArray.put(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String res = HttpUtil.execute(url, new HttpParameters(json), App.getInstance().getCurrentToken());
        int errorCode = StringUtil.getHttpResponseErrorCode(res);
        if (errorCode == 401) {
            String newToken = new TokenHelper().getTokenSync();
            res = HttpUtil.execute(url, new HttpParameters(json),
                    newToken);
        }
        try {
            if (res.startsWith("[")) {
                JSONArray resArray = new JSONArray(res);
                if (resArray != null) {
                    ArrayList<Long> ids = new ArrayList<>();
                    for (int i = 0; i < resArray.length(); i++) {
                        ids.add(resArray.optLong(i));
                    }
                    return ids;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private long postWord(WordData word, String content) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/users/current/contribute";
        JSONObject json = new JSONObject();
        try {
            if (word.getColumnIdList() != null && word.getColumnIdList().size() > 0) {
                json.put("column_id", word.getColumnIdList().get(0));
                JSONArray linkIdArr = new JSONArray();
                json.put("link_column_ids", linkIdArr);
                for (int i = 1; i < word.getColumnIdList().size(); i++) {
                    linkIdArr.put(word.getColumnIdList().get(i));
                }
            }
            json.put("title", word.getTitle());
            json.put("body", content);
            String res = HttpUtil.execute(url, new HttpParameters(json),
                    App.getInstance().getCurrentToken());
            int errorCode = StringUtil.getHttpResponseErrorCode(res);
            if (errorCode == 401) {
                String newToken = new TokenHelper().getTokenSync();
                res = HttpUtil.execute(url, new HttpParameters(json),
                        newToken);
            }
            long id = Long.valueOf(res);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setPostWordObserver(Observer<WordData> postWordObserver) {
        this.postWordObserver = postWordObserver;
    }
}
