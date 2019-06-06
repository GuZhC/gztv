package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;

import java.io.Serializable;

/**
 * 电视剧集的每个视频
 */
public class ItemTVSeriesVideo implements IItemVideo, Serializable {
    /**
     * Video Id
     */
    private long id;

    private int index;

    private String title;

    private boolean isPlaying;

    private VideoContent videoContent;

    public ItemTVSeriesVideo() {

    }

    public ItemTVSeriesVideo(long id, int index) {
        this.id = id;
        this.index = index;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getItemIndex() {
        return index;
    }

    @Override
    public String getTitle() {
        if (TextUtils.isEmpty(title)) {
            title = (index + 1) + "";
        }
        return title;
    }

    @Override
    public Observable<VideoContent> getVideoInfo() {
        return autoGetVideoUri();
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    /**
     * 自动网络补全视频地址
     *
     * @return
     */
    public Observable<VideoContent> autoGetVideoUri() {
        if (videoContent == null) {
            try {
                if (id == 0) {
                    Log.e("TAG", "get video source error----------");
                }
                String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/body-components";
                JSONObject param = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(id);
                param.put("videos", array);
                String res = HttpUtil.execute(url, new HttpParameters(param), App.getInstance().getCurrentToken());
                JSONObject resJson = new JSONObject(res);
                JSONArray videoJsonArr = resJson.optJSONArray("videos");
                JSONObject item0 = videoJsonArr.optJSONObject(0);
                VideoContent content = new Gson().fromJson(item0.toString(), VideoContent.class);
                if (content.getId() != 0) {
                    videoContent = content;
                    Log.d("TAG", "get video source ok -------------");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Observable.just(videoContent);
    }

    public VideoContent getVideoContent() {
        return videoContent;
    }

    public void setVideoContent(VideoContent videoContent) {
        this.videoContent = videoContent;
    }
}
