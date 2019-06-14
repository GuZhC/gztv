package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressRequestListener;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.*;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.fragment.FileFragment;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.util.LogUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.business.NetIPManager;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.selectedmedia.MediaModel;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * create by  heyang  2016-12/21  内容 CMS
 */

public class ContentCmsApi {
    private DataRequest.DataCallback callback;
    private LoginParams loginParams;
    private Context mContext;
    public String severUrl = "";
    public String pictureUrl = "";
    public String videoUrl = "";

    public ContentCmsApi(Context context) {
        mContext = context;
        severUrl = App.getInstance().getmSession().getContentcmsServerUrl();
        pictureUrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/pictures/uploader";
        videoUrl = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/videos/uploader";
    }

    /*
    *  获取图片的上传地址
     */
//    public String getImaUpfileUrl() {
//        String result = "";
//        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), App.getInstance().getCurrentToken());
//        return result;
//    }

    public String getImaUpfileUrl() throws ApiException {
        String res = HttpUtil.executeGet(pictureUrl, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(res);
        return UtilHelp.checkUrl(res);
    }

    /*
   *  获取视频的上传地址
    */
//    public String getVideoUpfileUrl() throws ApiException {
//        String result = "";
//        result = HttpUtil.executeGet(videoUrl, new HttpParameters(), App.getInstance().getCurrentToken());
//        LogUtils.e(FileFragment.TAG, "getVideoUpfileUrl  result 1=" + result);
//        if (result != null && !TextUtils.isEmpty(result)) {
//            result = UtilHelp.checkUrl(result);
//            LogUtils.e(FileFragment.TAG, "getVideoUpfileUrl  result 2=" + result);
//        }
//        return result;
//    }

    public String getVideoUpfileUrl() throws ApiException {
        String res = HttpUtil.executeGet(videoUrl, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(res);
        return UtilHelp.checkUrl(res);
    }

    /**
     * 收藏 / 取消收藏 主题
     */
    public void farityToptic(long contentId, boolean flag, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents/" + contentId + "/favor";
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    return true;
                }
                return false;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.POST).
                                setBooleanParams(flag).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 是否被收藏
     *
     * @param contentId
     * @return
     */
    public boolean isFav(long contentId) {
        boolean flag = false;
        String url = severUrl + "/public/contents/favored/" + contentId;
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        try {
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                String token = UtilHelp.checkTokenError(json);
                if (token != null) {
                    HttpUtil.executeGet(url, new HttpParameters(), token);
                }
                flag = json.optBoolean(String.valueOf(contentId));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 给内容评论点赞
     *
     * @param commendId
     * @return
     */
    public void praiseforCmsCommend(View imag, long commendId, DataRequest.DataCallbackTag callbackTag) {
        String url = severUrl + "/public/comments/" + commendId + "/like";
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    return true;
                }
                return false;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.POST).
                                setTagView(imag).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callbackTag);
    }


    /***
     * 根据图片id, 获取图文的图片 （旧接口）
     * @param id
     * @return
     */
//    public ContentCmsInfoEntry.GroupImgsBean getGroupBeanById(int id) {
//        ContentCmsInfoEntry.GroupImgsBean entry = null;
//        String url = severUrl + "/public/pictures/" + id;
//        try {
//            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
//            JSONObject json = JsonCreater.jsonParseString(response);
//            if (json != null) {
//                JSONArray arr = json.optJSONArray("result");
//                if (arr != null && arr.length() > 0) {
//                    JSONObject ob = (JSONObject) arr.get(0);
//                    if (ob != null)
//                        entry = new Gson().fromJson(ob.toString(), ContentCmsInfoEntry.GroupImgsBean.class);
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return entry;
//    }

    /**
     * 同步获取图文的图片
     *
     * @param ids
     * @return
     */
    public ArrayList<ContentCmsInfoEntry.GroupImgsBean> getGroupBeanById(Long... ids) {
        ArrayList<ContentCmsInfoEntry.GroupImgsBean> dlist = new ArrayList<>();
        String url = severUrl + "/public/body-components";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    sourceArr.put(ids[i]);
                }
            }
            params.put("pictures", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.exePost(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                JSONArray arr = json.optJSONArray("pictures");
                if (arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ob = (JSONObject) arr.get(i);
                        if (ob != null) {
                            ContentCmsInfoEntry.GroupImgsBean entrys = new Gson().fromJson(ob.toString(), ContentCmsInfoEntry.GroupImgsBean.class);
                            dlist.add(entrys);
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }


    /***
     * 根据图片id,同步获取网页視頻实体类
     * @param id
     * @return
     */
    public ContentCmsInfoEntry.VideosBean getWebVideoBeanById(long id) {
        ContentCmsInfoEntry.VideosBean tag = null;
        String url = severUrl + "/public/videos/" + id;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                JSONArray arr = json.optJSONArray("result");
                if (arr != null && arr.length() > 0) {
                    JSONObject ob = (JSONObject) arr.get(0);
                    if (ob != null) {
                        JSONArray varr = ob.optJSONArray("versions");
                        if (varr != null && varr.length() > 0) {
                            tag = new ContentCmsInfoEntry.VideosBean();
                            Gson g = new Gson();
                            for (int i = 0; i < varr.length(); i++) {
                                JSONObject item = (JSONObject) varr.get(i);
                                ContentCmsInfoEntry.VersionsBean entry = g.fromJson(item.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                if (entry != null) {
                                    int prefix = entry.getUrl().lastIndexOf(".");
                                    if (entry.getUrl() != null &&
                                            TextUtils.equals(entry.getUrl().toString().substring(prefix), ".mp4")) {
                                        tag.setVersions(entry);
                                        tag.setId(id);
                                        tag.setCoverUrl(ob.optString("cover_url"));
                                        break;
                                    }
                                }
                            }
                            tag.setDuration(ob.optLong("duration"));
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tag;
    }

    /***
     * 根据图片id,同步获取网页視頻实体类
     * @param ids
     * @return
     */
    public ArrayList<ContentCmsInfoEntry.VideosBean> getWebVideoBeanById(Long... ids) {
        ArrayList<ContentCmsInfoEntry.VideosBean> dlist = new ArrayList<>();
        String url = severUrl + "/public/body-components/";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    sourceArr.put(ids[i]);
                }
            }
            params.put("videos", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.exePost(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                JSONArray arr = json.optJSONArray("videos");
                if (arr != null && arr.length() > 0) {
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject ob = (JSONObject) arr.get(k);
                        if (ob != null) {
                            JSONArray varr = ob.optJSONArray("versions");
                            long id = ob.optLong("id");
                            if (varr != null && varr.length() > 0) {
                                ContentCmsInfoEntry.VideosBean tag = new ContentCmsInfoEntry.VideosBean();
                                Gson g = new Gson();
                                for (int i = 0; i < varr.length(); i++) {
                                    JSONObject item = (JSONObject) varr.get(i);
                                    ContentCmsInfoEntry.VersionsBean entry = g.fromJson(item.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                    if (entry != null) {
                                        int prefix = entry.getUrl().lastIndexOf(".");
                                        if (entry.getUrl() != null &&
                                                TextUtils.equals(entry.getUrl().toString().substring(prefix), ".mp4")) {
                                            tag.setVersions(entry);
                                            tag.setId(id);
                                            tag.setCoverUrl(ob.optString("cover_url"));
                                            break;
                                        }
                                    }
                                }
                                dlist.add(tag);
                            }
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /***
     * 根据图片id,同步获取网页音频实体类
     * @param ids
     * @return
     */
    public ArrayList<ContentCmsInfoEntry.VideosBean> getWebAudioBeanById(Long... ids) {
        ArrayList<ContentCmsInfoEntry.VideosBean> dlist = new ArrayList<>();
        String url = severUrl + "/public/body-components/";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    sourceArr.put(ids[i]);
                }
            }
            params.put("audios", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.exePost(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                JSONArray arr = json.optJSONArray("audios");
                if (arr != null && arr.length() > 0) {
                    JSONObject ob = (JSONObject) arr.get(0);
                    if (ob != null) {
                        JSONArray varr = ob.optJSONArray("versions");
                        long id = ob.optLong("id");
                        if (varr != null && varr.length() > 0) {
                            ContentCmsInfoEntry.VideosBean tag = new ContentCmsInfoEntry.VideosBean();
                            Gson g = new Gson();
                            for (int i = 0; i < varr.length(); i++) {
                                JSONObject item = (JSONObject) varr.get(i);
                                ContentCmsInfoEntry.VersionsBean entry = g.fromJson(item.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                if (entry != null) {
                                    int prefix = entry.getUrl().lastIndexOf(".");
                                    if (entry.getUrl() != null &&
                                            TextUtils.equals(entry.getUrl().toString().substring(prefix), ".mp3")) {
                                        tag.setVersions(entry);
                                        tag.setId(id);
                                        tag.setCoverUrl(ob.optString("cover_url"));
                                        break;
                                    }
                                }
                            }
                            dlist.add(tag);
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /***
     * 根据图片id,同步获取点播視頻实体类
     * @param id
     * @return
     */
    public ContentCmsInfoEntry.VersionsBean getVideoBeanById(long id) {
        ContentCmsInfoEntry.VersionsBean entry = null;
        String url = severUrl + "/public/body-components/";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            sourceArr.put(id);
            params.put("videos", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.execute(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                JSONArray arr = json.optJSONArray("videos");
                if (arr != null && arr.length() > 0) {
                    JSONObject ob = (JSONObject) arr.get(0);
                    if (ob != null) {
                        JSONArray varr = ob.optJSONArray("versions");
                        if (varr != null && varr.length() > 0) {
                            Gson g = new Gson();
                            for (int i = 0; i < varr.length(); i++) {
                                JSONObject item = (JSONObject) varr.get(i);
                                entry = g.fromJson(item.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                if (entry != null) {
                                    entry.setThumb(ob.optString("cover_url"));
                                    String urlpath = entry.getUrl();
                                    if (!TextUtils.isEmpty(urlpath)) {
                                        String prefix  =  FileUtil.getSuffix(urlpath.toLowerCase().toString());
                                        if (TextUtils.equals(prefix, ".mp4")) {
                                            String name = item.optString("name");  //待判断的字符串
                                            if (!TextUtils.isEmpty(name)) {
                                                String reg = ".*源版本.*";  //判断字符串中是否含有特定字符串ll
                                                if (!name.matches(reg)) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entry;
    }

    /***
     * 根据直播id,同步获取電視直播 (旧接口)
     * @param id
     * @return
     */
//    public ContentCmsInfoEntry.LiveBean getTvLiveById(int id) {
//        ContentCmsInfoEntry.LiveBean bean = null;
//        String url = severUrl + "/public/lives/" + id;
//        try {
//            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
//            JSONObject exObj = JsonCreater.jsonParseString(response);
//            if (exObj != null) {
////                if (exObj.has("m3u8Url")) {
////                    urlpath = exObj.optString("m3u8Url");
////                }
////                if (TextUtils.equals(urlpath, "null") && !TextUtils.isEmpty(urlpath)) {
////                    urlpath = exObj.optString("flvUrl");
////                }
////                if (TextUtils.equals(urlpath, "null") && !TextUtils.isEmpty(urlpath)) {
////                    urlpath = exObj.optString("url");
////                }
//////                if (exObj.has("id")) {
//////                    long id = exObj.optLong("id");
//////                    entry.setLiveId(id);
//////                }
////                JSONArray vers = exObj.optJSONArray("versions");
////                if (vers != null && vers.length() > 0) {
////                    try {
////                        for (int i = 0; i < vers.length(); i++) {
////                            JSONObject obj = (JSONObject) vers.get(i);
////                            if (obj != null) {
////                                urlpath = obj.optString("url");
////                                String prefix = urlpath.substring(urlpath.lastIndexOf(".") + 1);
////                                if (TextUtils.equals(prefix, "mp4")) break;
////                            }
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
//                JSONArray arr = exObj.optJSONArray("result");
//                if (arr != null && arr.length() > 0) {
//                    Gson g = new Gson();
//                    for (int i = 0; i < arr.length(); i++) {
//                        JSONObject ob = (JSONObject) arr.get(i);
//                        if (ob != null) {
//                            bean = g.fromJson(ob.toString(), ContentCmsInfoEntry.LiveBean.class);
//                            break;
//                        }
//                    }
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return bean;
//    }
    public ContentCmsInfoEntry.LiveBean getTvLiveById(long id) {
        ContentCmsInfoEntry.LiveBean bean = null;
        String url = severUrl + "/public/body-components/";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            sourceArr.put(id);
            params.put("lives", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.exePost(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject exObj = JsonCreater.jsonParseString(response);
            if (exObj != null) {
                JSONArray arr = exObj.optJSONArray("lives");
                if (arr != null && arr.length() > 0) {
                    Gson g = new Gson();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ob = (JSONObject) arr.get(i);
                        if (ob != null) {
                            bean = g.fromJson(ob.toString(), ContentCmsInfoEntry.LiveBean.class);
                            break;
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }


    /**
     * 2018/1/19
     * 获取电视 广播的回放地址
     *
     * @param id
     * @param date
     * @return
     */
    public List<LiveProgramEntity.LiveProgram> getProgramBackById(long id, String date) {
        List<LiveProgramEntity.LiveProgram> dlist = null;
        String url = severUrl + "/public/lives/" + id + "/playbacks/" + date;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject exObj = JsonCreater.jsonParseString(response);
            if (exObj != null) {
                JSONArray arr = exObj.optJSONArray("segments");
                if (arr != null && arr.length() > 0) {
                    dlist = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ob = (JSONObject) arr.get(i);
                        if (ob != null) {
                            LiveProgramEntity.LiveProgram program = new LiveProgramEntity.LiveProgram();
                            program.setStart_time(ob.optLong("start_time"));
                            program.setStop_time(ob.optLong("stop_time"));
                            program.setM3u8_url(ob.optString("playback_url"));
                            dlist.add(program);
                        }
                    }

                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /***
     * 根据图组id,同步获取图组(旧接口)
     * @param id
     * @return
     */
//    public List<ContentCmsInfoEntry.GroupImgsBean> getGroupsById(int id) {
//        List<ContentCmsInfoEntry.GroupImgsBean> dlist = null;
//        String url = severUrl + "/public/picturesets/" + id;
//        try {
//            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
//            JSONObject exObj = JsonCreater.jsonParseString(response);
//            if (exObj != null) {
//                JSONArray arr = exObj.optJSONArray("result");
//                if (arr != null && arr.length() > 0) {
//                    JSONObject ob = (JSONObject) arr.get(0);
//                    if (ob != null) {
//                        JSONArray groups = ob.optJSONArray("pictures");
//                        if (groups != null && groups.length() > 0) {
//                            dlist = new ArrayList<ContentCmsInfoEntry.GroupImgsBean>();
//                            Gson g = new Gson();
//                            for (int i = 0; i < groups.length(); i++) {
//                                JSONObject obs = (JSONObject) groups.get(i);
//                                if (obs != null) {
//                                    ContentCmsInfoEntry.GroupImgsBean bean = g.fromJson(obs.toString(), ContentCmsInfoEntry.GroupImgsBean.class);
//                                    dlist.add(bean);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return dlist;
//    }

    /***
     * 根据图组id,同步获取图组
     * @param id
     * @return
     */
    public List<ContentCmsInfoEntry.GroupImgsBean> getGroupsById(long id) {
        List<ContentCmsInfoEntry.GroupImgsBean> dlist = null;
        String url = severUrl + "/public/body-components/";
        try {
            JSONObject params = new JSONObject();
            JSONArray sourceArr = new JSONArray();
            sourceArr.put(id);
            params.put("picturesets", sourceArr);
            HttpParameters httpParameters = new HttpParameters();
            httpParameters.setJsonParams(params);
            String response = HttpUtil.exePost(url, httpParameters, App.getInstance().getCurrentToken());
            JSONObject exObj = JsonCreater.jsonParseString(response);
            if (exObj != null) {
                JSONArray arr = exObj.optJSONArray("picturesets");
                if (arr != null && arr.length() > 0) {
                    JSONObject ob = (JSONObject) arr.get(0);
                    if (ob != null) {
                        JSONArray groups = ob.optJSONArray("pictures");
                        if (groups != null && groups.length() > 0) {
                            dlist = new ArrayList<ContentCmsInfoEntry.GroupImgsBean>();
                            Gson g = new Gson();
                            for (int i = 0; i < groups.length(); i++) {
                                JSONObject obs = (JSONObject) groups.get(i);
                                if (obs != null) {
                                    ContentCmsInfoEntry.GroupImgsBean bean = g.fromJson(obs.toString(), ContentCmsInfoEntry.GroupImgsBean.class);
                                    dlist.add(bean);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /**
     * 获取cms详情，解析json,获取showType类型
     */
    public int getModeType(String type, int thumbe_size) {
        int modeType = 1;
        if (TextUtils.equals(type, "video")) {
            modeType = 2;
        } else if (TextUtils.equals(type, "live")) {  //电视广播内容
            modeType = 9;
        } else if (TextUtils.equals(type, "show")) {
            modeType = 3;
        } else if (TextUtils.equals(type, "groups") || TextUtils.equals(type, "pictureset")) {
            modeType = 4;
        } else if (TextUtils.equals(type, "ad") ||
                TextUtils.equals(type, "link") || TextUtils.equals(type, "adtype")) {
            modeType = 6;
        } else if (TextUtils.equals(type, "signup")
                || TextUtils.equals(type, "questionnaire")
                || TextUtils.equals(type, "vote")) {
            modeType = 7;
//            entry.setUrl(App.getInstance().getContentShareUrl() + "/" + entry.getId());
        }else if (TextUtils.equals(type, "special")) {
            modeType = 11;
        } else if (TextUtils.equals(type, "banner")) {
            modeType = 12;
        }
        if (modeType == 1) {
            if (thumbe_size >= 3) modeType = 8;   //有点重叠 ，图组 和一般  新闻
            if (thumbe_size == 0)
                modeType = 5;       // 有点重叠 一般无缩略图  新闻
        }
        return modeType;
    }

    /**
     * 获取显示模式
     */
    public int getShowModeType(int type, String typecode) {
        //0-无图 1-单图 2-大图 3-多图
//        int showTy = 6;
//        switch (type) {
//            case 1:
//                showTy = 1;
//                break;
//            case 2:
//                showTy = 2;
//                break;
//            case 3:
//                showTy = 4;
//                break;
        //        int showTy = 6;
        int showTy = 1;
        switch (type) {
            case 0:
                showTy = 5;  //无图模式
                break;
            case 2:
                showTy = 2;
                break;
            case 3:
                showTy = 4;
                break;
        }
        //特殊處理廣告
        if (TextUtils.equals(typecode.toLowerCase().trim(), "ad")
                || TextUtils.equals(typecode.toLowerCase().trim(), "banner")) {
            showTy = 6;
        } else if (TextUtils.equals(typecode.toLowerCase().trim(), "special")) {
            showTy = 11;
        }else if (TextUtils.equals(typecode.toLowerCase().trim(), "emergency")){    //添加应急消息类型
            showTy = 13;
        }
        return showTy;
    }

    /***
     * 解析json  得到 ContentCmsInfoEntry
     *
     * @param
     * @return
     */
    public ContentCmsInfoEntry getEnteyFromJson(long id) {
        ContentCmsInfoEntry entry = null;
        String url = severUrl + "/public/contents/" + id;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            Log.e( "getEnteyFromJson: ", response);
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())
                    && !TextUtils.equals(jsonObject.toString(), "{}")) {
                entry = new Gson().fromJson(jsonObject.toString(), ContentCmsInfoEntry.class);
//                JSONObject fieldObj = jsonObject.optJSONObject("fields");
//                if (fieldObj != null) {
//                    ContentCmsInfoEntry.Quickentry quickentry = new ContentCmsInfoEntry.Quickentry();
//                    quickentry.setType(fieldObj.optString("type"));
//                    quickentry.setRelated_content_id(fieldObj.optLong("related_content_id"));
//                    quickentry.setRelated_show_id(fieldObj.optLong("related_show_id"));
//                    if (TextUtils.equals(quickentry.getType(), "show")) {
//                        // 如果是直播，还得批量获取直播的状态
//                        LiveInfo info = getLiveInfoStatus(quickentry.getRelated_show_id());
//                        quickentry.setLivInfo(info);
//                    }
//                    if (TextUtils.equals(quickentry.getType(), "cms")) {
//                        ContentCmsInfoEntry entry1 = getEnteyFromJson(quickentry.getRelated_content_id());
//                        int modeType = getModeType(entry1.getType(), 0);
//                        entry1.setShowType(modeType);
//                        quickentry.setContentInfo(entry1);
//                    }
//                    entry.setQuickentry(quickentry);
//                }
                int modeType = getModeType(entry.getType(), entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0);
                entry.setShowType(modeType);
                JSONObject exObj = null;
                if (TextUtils.equals(entry.getType(),"audio")){
                    Long audio_id = 0L;
                    JSONObject field = jsonObject.optJSONObject("fields");
                    if (field != null && field.has("audio_id")){
                        audio_id = field.optLong("audio_id");
                        //拼接
                        JSONObject body = jsonObject.optJSONObject("body_components");
                        if (body != null){
                            JSONArray array = body.optJSONArray("audios");
                            if (array != null){
                                array.put(audio_id);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder().append("<br /><!--AUDIO#").append(audio_id).append(",0,0--><br />");
                        entry.setBody(stringBuilder.toString() + entry.getBody());
//                        entry.setResponse(new Gson().toJson(entry));
                    }


                }
                if (modeType == 1 || modeType == 5
                        || modeType == 8) {
                    exObj = jsonObject.optJSONObject("body_components");
                } else {
//                    exObj = jsonObject.optJSONObject("extension");
//                    if (exObj == null)
//                        exObj = jsonObject.optJSONObject("fields");
                    exObj = getCustomExtends(jsonObject);
                }
                if (exObj != null) {
                    if (modeType == 2) {
                        if (exObj.has("video_id")) {
                            long vid = exObj.optLong("video_id");
                            List<Long> videosList = new ArrayList<>();
                            videosList.add(vid);
                            getRaltioComponentsIds(null, videosList, null, entry);
                            if (!(entry.getVideoGroups() == null || entry.getVideoGroups().isEmpty())) {
                                ContentCmsInfoEntry.VideosBean bean = entry.getVideoGroups().get(0);
                                if (bean != null) {
                                    if (bean.getVersions() != null) {
                                        entry.setUrl(bean.getVersions().getUrl());
                                    }
                                    entry.setVideoThumb(bean.getCoverUrl());
                                }
                            }
//                            ContentCmsInfoEntry.VersionsBean bean = getVideoBeanById(vid);
//                            if (bean != null) {
//                                entry.setUrl(bean.getUrl());
//                                entry.setVideoThumb(bean.getThumb());
//                            }
                        }
                    } else if (modeType == 3 || modeType == 9) {
                        if (exObj.has("live_id")) {
                            long vid = exObj.optLong("live_id");
                            ContentCmsInfoEntry.LiveBean bean = getTvLiveById(vid);
                            if (bean != null) {
                                entry.setLiveId(bean.getId());
//                            String liveurl = bean.getM3u8_url();
//                            if (bean.getM3u8_url() == null || TextUtils.isEmpty(bean.getM3u8_url())) {
//                                liveurl = bean.getFlv_url();
//                            }
                                //电视，广播直播优先使用flv地址
                                String liveurl = bean.getFlv_url();
                                if (liveurl == null || TextUtils.isEmpty(liveurl)) {
                                    liveurl = bean.getM3u8_url();
                                }
                                entry.setUrl(liveurl);
                            }
                        }
                    } else if (modeType == 4) {
                        if (exObj.has("pictureset_id")) {
                            long vid = exObj.optLong("pictureset_id");
                            List<ContentCmsInfoEntry.GroupImgsBean> list = getGroupsById(vid);
                            if (list != null && list.size() > 0) {
                                entry.setGroupimgs(list);
                            }
                        }
                    } else if (modeType == 6) {
                        if (exObj.has("link_url")) {
                            String wen = exObj.optString("link_url");
                            entry.setUrl(wen);
//                            entry.setSource(wen);
                        }
                    } else if (modeType == 7) {
                        entry.setUrl(App.getInstance().getContentShareUrl() + entry.getId());
                    }else if (modeType == 12) {
                        if (exObj.has("banner_external_link")) {
                            String urls = exObj.optString("banner_external_link");
                            if (TextUtils.isEmpty(urls)) {
                                entry.setShowType(-1);
                            } else {
                                entry.setUrl(urls);
                                entry.setId(-1);
                                entry.setShowType(6);
                            }
                        } else {
                            entry.setShowType(-1);
//                            long ids = exObj.optLong("banner_internal_link");
//                            entry.setId(ids);
                        }
                    }
//                    else if (modeType == 9) {
                    //电视广播直播
//                    }
                    else {
                        parseWebCommpants(exObj, entry);
                    }
                }
//                    entry.setShowType(modeType);  //defaultc
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return entry;
    }

    /**
     * 解析嵌入式组件
     *
     * @param exObj
     * @param entry
     */
    public void parseWebCommpants(JSONObject exObj, ContentCmsInfoEntry entry) throws JSONException {
        List<Long> pirtureList = null;
        if (!exObj.isNull("pictures")) {
            JSONArray arr = exObj.optJSONArray("pictures");
            if (arr != null && arr.length() > 0) {
                pirtureList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    pirtureList.add(arr.getLong(i));
                }
            }
        }

        List<Long> videosList = null;
        if (!exObj.isNull("videos")) {
            JSONArray arr = exObj.optJSONArray("videos");
            if (arr != null && arr.length() > 0) {
                videosList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    videosList.add(arr.getLong(i));
                }
            }
        }

        List<Long> audioList = null;
        if (!exObj.isNull("audios")) {
            JSONArray arr = exObj.optJSONArray("audios");
            if (arr != null && arr.length() > 0) {
                audioList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    audioList.add(arr.getLong(i));
                }
            }
        }
        getRaltioComponentsIds(pirtureList, videosList, audioList, entry);
    }

    /**
     * 批量获取嵌入式组件   ============================================
     */
    public void getRaltioComponentsIds(List<Long> piclist, List<Long> videoList,
                                       List<Long> audiolist, ContentCmsInfoEntry entry) {
        try {
            JSONArray picObj = null;
            JSONArray videoObj = null;
            JSONArray audioObj = null;
            if (!(piclist == null || piclist.isEmpty())) {
                picObj = new JSONArray();
                for (int i = 0; i < piclist.size(); i++) {
                    picObj.put(piclist.get(i));
                }
            }
            if (!(videoList == null || videoList.isEmpty())) {
                videoObj = new JSONArray();
                for (int i = 0; i < videoList.size(); i++) {
                    videoObj.put(videoList.get(i));
                }
            }
            if (!(audiolist == null || audiolist.isEmpty())) {
                audioObj = new JSONArray();
                for (int i = 0; i < audiolist.size(); i++) {
                    audioObj.put(audiolist.get(i));
                }
            }
            JSONObject obj = new JSONObject();
            if (picObj != null)
                obj.put("pictures", picObj);
            if (videoObj != null)
                obj.put("videos", videoObj);
            if (audioObj != null)
                obj.put("audios", audioObj);
//            String url = severUrl + "/public/components";
            String url = severUrl + "/public/body-components";
            String response = HttpUtil.execute(url, new HttpParameters(obj), App.getInstance().getCurrentToken());
            JSONObject json = null;
            json = JsonCreater.jsonParseString(response);
            if (json != null) {
                Gson gs = new Gson();
                if (json.has("pictures")) {
                    JSONArray picArr = json.optJSONArray("pictures");
                    if (!(picArr == null || picArr.length() == 0)) {
                        List<ContentCmsInfoEntry.GroupImgsBean> dlist = new ArrayList<ContentCmsInfoEntry.GroupImgsBean>();
                        for (int i = 0; i < picArr.length(); i++) {
                            JSONObject item = (JSONObject) picArr.get(i);
                            ContentCmsInfoEntry.GroupImgsBean bean = gs.fromJson(item.toString(), ContentCmsInfoEntry.GroupImgsBean.class);
                            if (bean == null) continue;
                            dlist.add(bean);
                        }
                        entry.setGroupimgs(dlist);
                    }
                }
                if (json.has("videos")) {
                    JSONArray videoArr = json.optJSONArray("videos");
                    if (!(videoArr == null || videoArr.length() == 0)) {
                        List<ContentCmsInfoEntry.VideosBean> dlist = new ArrayList<ContentCmsInfoEntry.VideosBean>();
                        ContentCmsInfoEntry.VideosBean bean = null;
                        for (int i = 0; i < videoArr.length(); i++) {
                            JSONObject item = (JSONObject) videoArr.get(i);
                            JSONArray varr = item.optJSONArray("versions");
                            if (varr != null && varr.length() > 0) {
                                bean = new ContentCmsInfoEntry.VideosBean();
                                bean.setId(item.optLong("id"));
                                bean.setCoverUrl(item.optString("cover_url"));
                                Gson g = new Gson();
                                Map<String, ContentCmsInfoEntry.VersionsBean> verlist = new HashMap<>();
                                ContentCmsInfoEntry.VersionsBean srcTag = null;
                                for (int k = 0; k < varr.length(); k++) {
                                    JSONObject itemds = (JSONObject) varr.get(k);
                                    ContentCmsInfoEntry.VersionsBean entrys = g.fromJson(itemds.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                    String urlPath = entrys.getUrl().toLowerCase().toString();
                                    String dup = FileUtil.getSuffix(urlPath);
                                    if (entrys != null) {
                                        if (TextUtils.equals(entrys.getName(), "源版本"))
                                            srcTag = entrys;
                                        verlist.put(dup, entrys);
                                    }
                                }
                                if (!(verlist == null || verlist.isEmpty())) {
                                    ContentCmsInfoEntry.VersionsBean entrytag = verlist.get(".mp4");
                                    if (entrytag != null) {
                                        String name = entrytag.getName();  //待判断的字符串
                                        String reg = ".*源版本.*";  //判断字符串中是否含有特定字符串ll
                                        if (!name.matches(reg)) {
                                            bean.setVersions(entrytag);
                                        }
                                    }
                                    if (bean.getVersions() == null) {
                                        ContentCmsInfoEntry.VersionsBean entryu8 = verlist.get(".m3u8");
                                        if (entryu8 != null) {
                                            bean.setVersions(entryu8);
                                        } else {
                                            if (srcTag != null)
                                                bean.setVersions(srcTag);
                                        }
                                    }
                                }
                                dlist.add(bean);
                            }
                        }
                        entry.setVideoGroups(dlist);
                    }
                }
                if (json.has("audios")) {
                    JSONArray audioArr = json.optJSONArray("audios");
                    if (!(audioArr == null || audioArr.length() == 0)) {
                        List<ContentCmsInfoEntry.VideosBean> dlist = new ArrayList<ContentCmsInfoEntry.VideosBean>();
                        for (int i = 0; i < audioArr.length(); i++) {
                            JSONObject item = (JSONObject) audioArr.get(i);
                            ContentCmsInfoEntry.VideosBean bean = null;
//                            ContentCmsInfoEntry.VideosBean bean = gs.fromJson(item.toString(), ContentCmsInfoEntry.VideosBean.class);
//                            if (bean == null) continue;
                            JSONArray varr = item.optJSONArray("versions");
                            if (varr != null && varr.length() > 0) {
                                bean = new ContentCmsInfoEntry.VideosBean();
                                Gson g = new Gson();
                                for (int k = 0; k < varr.length(); k++) {
                                    JSONObject items = (JSONObject) varr.get(k);
                                    ContentCmsInfoEntry.VersionsBean entrys = g.fromJson(items.toString(), ContentCmsInfoEntry.VersionsBean.class);
                                    if (entrys != null) {
                                        String urlPath = entrys.getUrl().toLowerCase().toString();
                                        int prefix = urlPath.lastIndexOf(".");
                                        if (!TextUtils.isEmpty(urlPath) &&
                                                TextUtils.equals(urlPath.substring(prefix), ".mp3")) {
                                            bean.setVersions(entrys);
                                            bean.setId(item.optLong("id"));
                                            bean.setCoverUrl(item.optString("cover_url"));
                                            String name = items.optString("name");  //待判断的字符串
                                            String reg = ".*mp3.*";  //判断字符串中是否含有特定字符串ll
                                            if (name.matches(reg)) {
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                }
                                dlist.add(bean);
                            }
                        }
                        entry.setAduioGroups(dlist);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id ，批量获取
     *
     * @param id
     * @return
     */
    public List<ContentCmsEntry> getMulitiEntry(long id) {
        List<ContentCmsEntry> list = null;
        String url = severUrl + "/public/contents/multi/" + id;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                JSONArray result = jsonObject.optJSONArray("result");
                if (result != null) {
                    list = new ArrayList<ContentCmsEntry>();
                    Gson g = new Gson();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = (JSONObject) result.get(i);
                        ContentCmsEntry entry = g.fromJson(item.toString(), ContentCmsEntry.class);
                        //判断是不是多图
                        int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                        entry.setShowType(getShowModeType(entry.getList_item_mode(), entry.getType()));
                        int modeType = getModeType(entry.getType(), thumn_size);
//                        if (modeType == 3) {
//                             show 直播 特殊处理
//                            entry.setShowType(modeType);
//                        }
                        entry.setModeType(modeType);
                        list.add(entry);
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取相关的图文 视频 信息
     *
     * @param id
     * @param type
     * @return
     */
    public List<ContentCmsEntry> getRelationContenList(long id, String type,int count) {
        List<ContentCmsEntry> list = null;
        String url = severUrl + "/public/contents/" + id + "/correlates?types=" + type + "&count="+count;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                JSONArray result = jsonObject.optJSONArray("result");
                if (result != null) {
                    list = new ArrayList<ContentCmsEntry>();
                    Gson g = new Gson();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = (JSONObject) result.get(i);
                        ContentCmsEntry entry = g.fromJson(item.toString(), ContentCmsEntry.class);
                        //判断是不是多图
                        int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                        entry.setShowType(getShowModeType(entry.getList_item_mode(), entry.getType()));
                        int modeType = getModeType(entry.getType(), thumn_size);
                        entry.setModeType(modeType);
                        list.add(entry);
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取相关的图文 视频 信息
     *
     * @param id
     * @param type
     * @return
     */
    public void getRelationCmsList(long id, String type, DataRequest.DataCallback<ArrayList<ContentCmsEntry>> callback) {
        String url = severUrl + "/public/contents/" + id + "/correlates?types=" + type + "&count=4";
        new DataRequest<ArrayList<ContentCmsEntry>>(mContext) {
            @Override
            public ArrayList<ContentCmsEntry> jsonToBean(JSONObject jsonObject) {
                ArrayList<ContentCmsEntry> list = null;
                try {
                    if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                        JSONArray result = jsonObject.optJSONArray("result");
                        if (result != null) {
                            list = new ArrayList<ContentCmsEntry>();
                            Gson g = new Gson();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = (JSONObject) result.get(i);
                                ContentCmsEntry entry = g.fromJson(item.toString(), ContentCmsEntry.class);
                                //判断是不是多图
                                int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                                entry.setShowType(getShowModeType(entry.getList_item_mode(), entry.getType()));
                                int modeType = getModeType(entry.getType(), thumn_size);
                                entry.setModeType(modeType);
                                list.add(entry);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return list;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.GET).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);

    }


    /**
     * 获取点赞列表
     *
     * @param id
     * @return
     */
    public String getPraiseNumberList(long id) {
        String appendString = "";
        String url = severUrl + "/public/contents/" + id + "/attitudes?state=1&page=1&size=100";
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                JSONArray result = jsonObject.optJSONArray("data");
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = (JSONObject) result.get(i);
                        String name = item.optString("nickname");
                        if (!(name == null || TextUtils.isEmpty(name)
                                || TextUtils.equals(name, "null"))) {
                            appendString += name;
                            if (i != result.length() - 1)
                                appendString += " • ";
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return appendString;
    }

    /**
     * 批量获取直播的状态
     *
     * @param id
     * @return
     */
    public LiveInfo getLiveInfoStatus(long id) {
        LiveInfo liveState = null;
//        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/shows/" + id;
        String url = severUrl + "/public/shows/" + id;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
//            UtilHelp.checkError(jsonObject);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                JSONArray result = jsonObject.optJSONArray("data");
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = (JSONObject) result.get(i);
                        liveState = new Gson().fromJson(item.toString(), LiveInfo.class);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return liveState;
    }

    /*
    *   获取内容的详情 同步获取
     */
    public ContentCmsInfoEntry getContentCmsInfo(long tId) {
        String url = severUrl + "/public/contents/" + tId;
        ContentCmsInfoEntry entry = null;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject json = null;
            json = JsonCreater.jsonParseString(response);
            if (json != null) {
                entry = new Gson().fromJson(json.toString(), ContentCmsInfoEntry.class);
//                JSONObject exObj = json.optJSONObject("extension");  // 針對獲取電視直播
//                if (exObj == null)
//                    exObj = json.optJSONObject("fields");
                JSONObject exObj = getCustomExtends(json);
                if (exObj != null) {
                    if (exObj.has("live_id")) {
                        long id = exObj.optLong("live_id");
                        ContentCmsInfoEntry.LiveBean bean = getTvLiveById(id);
                        entry.setLiveId(bean.getId());
//                        if (exObj.has("next_live_id")) {
//                            long nextId = exObj.optLong("next_live_id");
//                            ContentCmsInfoEntry.LiveBean nextbean = getTvLiveById((int) nextId);
//                            if (nextbean != null)
//                                entry.setNextLiveName(nextbean.getName());
//                        }
                        String liveurl = bean.getFlv_url();
                        if (liveurl == null || TextUtils.isEmpty(liveurl)) {
                            liveurl = bean.getM3u8_url();
                        }
                        entry.setUrl(liveurl);
                    }
                }
//                JSONObject fieldObj = json.optJSONObject("fields");
                if (exObj != null) {
                    ContentCmsInfoEntry.Quickentry quickentry = new ContentCmsInfoEntry.Quickentry();
                    quickentry.setType(exObj.optString("type"));
                    quickentry.setRelated_content_id(exObj.optLong("related_content_id"));
                    quickentry.setRelated_show_id(exObj.optLong("related_show_id"));
                    if (TextUtils.equals(quickentry.getType(), "show")) {
                        // 如果是直播，还得批量获取直播的状态
                        LiveInfo info = getLiveInfoStatus(quickentry.getRelated_show_id());
                        quickentry.setLivInfo(info);
                    }
                    if (TextUtils.equals(quickentry.getType(), "cms")) {
                        ContentCmsInfoEntry entry1 = getEnteyFromJson(quickentry.getRelated_content_id());
                        int modeType = getModeType(entry1.getType(), 0);
                        entry1.setShowType(modeType);
                        quickentry.setContentInfo(entry1);
                    }
                    if (TextUtils.equals(quickentry.getType(), "column")) {
//                        quickentry.setType(exObj.optString("type"));
                        quickentry.setRelated_content_id(exObj.optLong("related_column_id"));
                    }
                    entry.setQuickentry(quickentry);
                }
            }
        } catch (ApiException e1) {
            e1.printStackTrace();
        }
        return entry;
    }

    public JSONObject getCustomExtends(JSONObject json) {
        JSONObject exObj = json.optJSONObject("extension");  // 針對獲取電視直播
        if (exObj == null || TextUtils.equals(exObj.toString(), "[]")
                || TextUtils.equals(exObj.toString(), "{}"))
            exObj = json.optJSONObject("fields");
        return exObj;
    }

    /**
     * 给内容 点赞
     */
    public void pubContentPraise(long cid, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents/" + cid + "/like";
        try {
            JSONObject obj = new JSONObject();
            String ip = App.getInstance().getLocalIp();
            obj.put("ip_address", ip);
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    if (json != null && !TextUtils.isEmpty(json.toString())) {
                        return true;
                    }
                    return false;
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                            .setUrl(url).
                                    setRequestType(DataReuqestType.POST).
                                    setJsonParams(obj).
                                    setToken(App.getInstance().getCurrentToken()).
                                    build(),
                    false).setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消内容点赞/点踩
     */
    public void cancelCmsPraise(long cid, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents/" + cid + "/cancel-attitude";
        try {
            JSONObject obj = new JSONObject();
            String ip = App.getInstance().getLocalIp();
            obj.put("ip_address", ip);
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    if (json != null && !TextUtils.isEmpty(json.toString())) {
                        return true;
                    }
                    return false;
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                            .setUrl(url).
                                    setRequestType(DataReuqestType.POST).
                                    setJsonParams(obj).
                                    setToken(App.getInstance().getCurrentToken()).
                                    build(),
                    false).setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取新闻的评论列表
     *
     * @param
     * @return
     */
    public ArrayList<CommendCmsEntry> getRootCommendListFromJson(JSONObject jsonObject) {
        if (jsonObject == null || TextUtils.isEmpty(jsonObject.toString())) return null;
        ArrayList<CommendCmsEntry> dlist = null;
        Map<Long, CommendCmsEntry> lists = null;
        List<CommendCmsEntry.SubCommentsBean> subLits = null;
        try {
            JSONArray result = jsonObject.optJSONArray("data");
            if (result != null) {
                dlist = new ArrayList<CommendCmsEntry>();
//                lists = new HashMap<Long, CommendCmsEntry>();
                Gson g = new Gson();
                for (int i = 0; i < result.length(); i++) {
//                            for (int i = 0; i < result.length(); i++) {
                    JSONObject item = (JSONObject) result.get(i);
                    CommendCmsEntry entry = g.fromJson(item.toString(), CommendCmsEntry.class);
                    JSONArray arr = item.optJSONArray("sub_comments");
                    if (arr != null && arr.length() > 0) {
                        subLits = new ArrayList<>();
                        for (int k = 0; k < arr.length(); k++) {
                            JSONObject ob = (JSONObject) arr.get(k);
                            CommendCmsEntry.SubCommentsBean subn = new Gson().fromJson(ob.toString(), CommendCmsEntry.SubCommentsBean.class);
                            subLits.add(subn);
                        }
                        entry.setmSubCommendList(subLits);
                        entry.setUser(false);
                    }
//
//                    if (entry.getRef_comments() != null && !entry.getRef_comments().isEmpty()) {
//                        for (CommendCmsEntry.RefCommentsBean bean : entry.getRef_comments()) {
//                            long id = bean.getId();
//                            CommendCmsEntry rp = lists.get(id);
//                            if (rp != null) {
//                                CommendCmsEntry.RefCommentsBean bs = new CommendCmsEntry.RefCommentsBean();
//                                bs.setAuthor_name(entry.getAuthor_name());
//                                bs.setAuthor_nickname(entry.getAuthor_nickname());
//                                bs.setText(entry.getText());
//                                List<CommendCmsEntry.RefCommentsBean> rpss = rp.getRef_comments();
//                                if (rpss == null) rpss = new ArrayList<CommendCmsEntry.RefCommentsBean>();
//                                rpss.add(bs);
//                                rp.setRef_comments(rpss);
//                            }
//                        }
//                    } else
//                        lists.put(entry.getId(), entry);
                    dlist.add(entry);
                }
//                if (lists != null && !lists.isEmpty()) {
//                    dlist = new ArrayList<CommendCmsEntry>();
//                    for (Long obj : lists.keySet()) {
//                        CommendCmsEntry value = lists.get(obj);
//                        dlist.add(value);
//                    }
//                    lists.clear();
//                }
                //时间倒序
//                if (dlist != null && dlist.size() > 0) {
//                    Collections.sort(dlist, new Comparator() {
//                        @Override
//                        public int compare(Object o1, Object o2) {
//                            return new Long(((CommendCmsEntry) o2).getCreation_time()).compareTo(new Long(((CommendCmsEntry) o1).getCreation_time()));
//                        }
//                    });
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /**
     * 给内容 踩
     */
    public void pubContentStamp(long cid, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents/" + cid + "/dislike";
        try {
            JSONObject obj = new JSONObject();
            String ip = App.getInstance().getLocalIp();
            obj.put("ip_address", ip);
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    if (json != null && !TextUtils.isEmpty(json.toString())) {
                        return true;
                    }
                    return false;
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                            .setUrl(url).
                                    setRequestType(DataReuqestType.POST).
                                    setJsonParams(obj).
                                    setToken(App.getInstance().getCurrentToken()).
                                    build(),
                    false).setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给内容发表评论
     */
    public void createCmsCommend(long cId, long tId, String content, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents/" + cId + "/comments";
        JSONObject obj = new JSONObject();
        try {
            obj.put("ref_comment_id", tId);
            obj.put("text", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DataRequest<Long>(mContext) {
            @Override
            public Long jsonToBean(JSONObject json) {
                long topicId = (long) -1;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    topicId = json.optLong("id");
                }
                return topicId;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.POST).
                                setJsonParams(obj).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 搜索内容
     */
    public void findAllByKey(String keyword, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/contents?keyword=" + keyword;
        new DataRequest<ArrayList<ContentCmsEntry>>(mContext) {
            @Override
            public ArrayList<ContentCmsEntry> jsonToBean(JSONObject json) {
                ArrayList<ContentCmsEntry> dlist = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    JSONArray arr = json.optJSONArray("data");
                    if (arr != null) {
                        dlist = new ArrayList<ContentCmsEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ContentCmsEntry entry = g.fromJson(object.toString(), ContentCmsEntry.class);
                                int thumb_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
//                                entry.setShowType(getModeType(entry.getType(), thumb_size));
                                entry.setModeType(getModeType(entry.getType(), thumb_size));
                                dlist.add(entry);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return dlist;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.GET).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                callback);
    }

    /**
     * 上传视频爆料
     */
    public void upVideoDiscluare(final String title, final String content, ArrayList<MediaModel> urls, final UIProgressRequestListener callbacface, Observer<Long> op) {
        final String url = severUrl + "/public/clue";
        LogUtils.e(FileFragment.TAG, "upVideoDiscluare    url=" + url);
        Observable.just(urls).
                observeOn(Schedulers.io()).
                map(new Func1<ArrayList<MediaModel>, Long>() {
                    @Override
                    public Long call(ArrayList<MediaModel> list) {
                        long topicId = -1;
                        String upFIleUrl = null;
                        try {
                            upFIleUrl = getVideoUpfileUrl();
                            LogUtils.e(FileFragment.TAG, "upVideoDiscluare  获取上传的 upFIleUrl=" + url);
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        if (upFIleUrl == null || TextUtils.isEmpty(upFIleUrl)) {
//                            try {
//                                throw new ApiException("获取视频上传地址失败!");
//                                return topicId;
//                            } catch (ApiException e) {
//                                e.printStackTrace();
//                            }
                            return topicId;
                        }
                        JSONObject parmaObj = new JSONObject();
                        JSONArray arr = new JSONArray();
                        JSONArray yy = new JSONArray();
                        if (list != null && !list.isEmpty()) {

                            for (int i = 0; i < list.size(); i++) {
                                MediaModel model = list.get(i);
                                LogUtils.e(FileFragment.TAG, "upVideoDiscluare  要上传的媒体路径 model.path=" + model.url);
                                String sharPath = synchronNewUpfile(upFIleUrl, model.url, callbacface);
                                LogUtils.e(FileFragment.TAG, "upVideoDiscluare 上传更换成功后   sharPath=" + sharPath);
                                if (!TextUtils.isEmpty(sharPath)) {
//                                    callbacface.MyObtainProgressValues(i * 20);
                                    yy.put(sharPath);
                                }
                            }
                        }
                        try {
                            parmaObj.put("paths", yy);
                            parmaObj.put("title", title);
                            parmaObj.put("content", content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LogUtils.e(FileFragment.TAG, "upVideoDiscluar  提交接口 start=" + parmaObj.toString());
                        JSONObject result = JsonHelper.httpPostJson(url, parmaObj, App.getInstance().getCurrentToken());
                        LogUtils.e(FileFragment.TAG, "upVideoDiscluar  提交接口完成  result=" + result);
                        if (result != null && !TextUtils.isEmpty(result.toString())) {
                            try {
                                UtilHelp.checkError(result);
                                topicId = result.optLong("id");
                            } catch (ApiException e) {
                                e.printStackTrace();
                                topicId = -1;
                            }
                        }
                        return topicId;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(op);
    }

    public String synchronNewUpfile(String upUrl, String path, UIProgressRequestListener lister) {
        String ralatPath = "";
        try {
            upUrl = UtilHelp.checkUrl(upUrl);
//            JSONObject json = JsonHelper.httpUpload(upUrl, path, null);
            String res = HttpUtil.uploadFileSynchronized(upUrl,
                    path, lister);
            if (res != null && !TextUtils.isEmpty(res)) {
//                int isOk = json.optInt("isOK");
//                if (isOk == 1) {
//                    ralatPath = json.optString("relativepath");
//                    ralatPath += json.optString("name");
//                }
                boolean isOk = false;
                JSONObject json = new JSONObject(res);
                isOk = json.optInt("isOK") > 0;
                String name = json.optString("name");
                String dir = json.optString("relativepath");
                ralatPath = dir + name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ralatPath;
    }

    public void onADClickNetRequest(final long adId, final long adContentId, final int adType) {
        Observable.create(NetIPManager.getCurrentNetIp())
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", adId);
                            json.put("ad_id", adContentId);
                            json.put("ad_type", adType);
                            json.put("ip_address", TextUtils.isEmpty(s) ? "" : s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String url = App.getInstance().getmSession().getAdServerUrl() + "/public/ads/click";
                        String res = HttpUtil.exePost(url, new HttpParameters(json), null);
                        boolean isOk = TextUtils.isEmpty(res);
                        Log.d("http", "ad click is ok ---- ip == " + s);
                        return isOk;
                    }
                })
                .subscribe();
    }

    /**
     * 获取列表位置广告
     *
     * @return
     */
    public Map<Integer, AdsEntry> getListAdsEntry(long columnId, int start, int end) {
        String url = App.getInstance().getmSession().getAdServerUrl() + "/public/list-ads/1/" + columnId + "/" + start + "/to/" + end;
        Map<Integer, AdsEntry> map = null;
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            JsonCreater.checkThrowError(jsonObject);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                Gson gs = new Gson();
                map = new HashMap<>();
                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String line = (String) iterator.next();
                    JSONObject ob = jsonObject.optJSONObject(line);
                    if (ob == null) continue;
                    AdsEntry entry = gs.fromJson(ob.toString(), AdsEntry.class);
                    JSONArray arr = ob.optJSONArray("show_pictures");
                    List<String> thumblist = new ArrayList<>();
                    if (!(arr == null || arr.length() == 0)) {
                        int i = 0;
                        //三图
                        do {
                            String thumb = (String) arr.get(i);
                            thumblist.add(thumb);
                            if (i >= 2 || entry.getList_mode() != 1) break;
                            i++;
                        } while (i < arr.length());
                    }
                    entry.setShowpicturesList(thumblist);
                    JSONObject ad = ob.optJSONObject("ad");
                    if (!(ad == null || TextUtils.isEmpty(ad.toString()))) {
                        List<AdsEntry.AdItem> list = new ArrayList<>();
                        if (ad != null && !TextUtils.isEmpty(ad.toString())) {
                            AdsEntry.AdItem adItem = gs.fromJson(ad.toString(), AdsEntry.AdItem.class);
                            UtilHelp.parseVideoVersions(ad, adItem, gs);
                            if (adItem != null) {
                                list.add(adItem);
                                entry.setAdItems(list);
                            }
                        }
                    }
                    map.put(Integer.parseInt(line), entry);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 批量同步获取图片
     */
    public ArrayList<ContentCmsEntry.EmergencyIcon> getPictures(Long[] longs){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < longs.length; i++) {
            stringBuilder.append(longs[i]).append(i == longs.length - 1 ? "" : ",");
        }
        String url = severUrl + "/public/pictures/" + stringBuilder.toString();
        String response = HttpUtil.executeGet(url,new HttpParameters(),null);
        ArrayList<ContentCmsEntry.EmergencyIcon> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++){
                    JSONObject object = (JSONObject) array.get(i);
                    ContentCmsEntry.EmergencyIcon icon = new Gson().fromJson(object.toString(), ContentCmsEntry.EmergencyIcon.class);
                    arrayList.add(icon);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }


}
