package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.ProgessCallbacface;
import com.dfsx.core.network.datarequest.*;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.model.Level;
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

import java.io.IOException;
import java.util.*;

/**
 * create by  heyang  2016-10-24
 */

public class TopicalApi {
    private DataRequest.DataCallback callback;
    private LoginParams loginParams;
    private Context mContext;
    public String severUrl = "";
    public String pictureUrl = "";
    public String videoUpurl = "";

    public TopicalApi(Context context) {
        mContext = context;
        severUrl = App.getInstance().getmSession().getCommunityServerUrl();
        pictureUrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/pictures/uploader";
        videoUpurl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/attachments/uploader";
    }

    /**
     * 获取图片的上传地址
     */
    public String getImaUpfileUrl() throws ApiException {
        String result = "";
        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(result);
        return UtilHelp.checkUrl(result);
//        if (result != null && !TextUtils.isEmpty(result)) {
//            try {
//                JSONObject obj = JsonCreater.jsonParseString(result);
//                if (obj != null) {
//                    String token = UtilHelp.checkTokenError(obj);
//                    if (token != null)
//                        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), token);
//                }
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
    }

    /**
     * 获取视频 的上传地址
     */
    public String getVideoUpfileUrl() throws ApiException {
        String result = "";
        result = HttpUtil.executeGet(videoUpurl, new HttpParameters(), App.getInstance().getCurrentToken());
        StringUtil.checkHttpResponseError(result);
        return UtilHelp.checkUrl(result);
    }

    /**
     * 给评论回复点赞
     *
     * @param commendId
     * @return
     */
    public void praiseforCommend(View imag, long commendId, DataRequest.DataCallbackTag callbackTag) {
        String url = severUrl + "/public/replies/" + commendId + "/like";
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

    /**
     * 获取所有栏目
     */
    public void getAllColumns(DataRequest.DataCallback callback) {
        String url = severUrl + "/public/columns";
        new DataRequest<ArrayList<ColumnEntry>>(mContext) {
            @Override
            public ArrayList<ColumnEntry> jsonToBean(JSONObject json) {
                ArrayList<ColumnEntry> dlist = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<ColumnEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ColumnEntry entry = g.fromJson(object.toString(), ColumnEntry.class);
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
     * 同步获取所有栏目
     *
     * @return
     */
    public List<ColumnEntry> getSynAllColumns() {
        List<ColumnEntry> dlist = null;
        String url = severUrl + "/public/columns";
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            if (response != null && !TextUtils.isEmpty(response)) {
                JSONObject obj = JsonCreater.jsonParseString(response);
                if (obj != null) {
                    JSONArray arr = obj.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<ColumnEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ColumnEntry entry = g.fromJson(object.toString(), ColumnEntry.class);
                                dlist.add(entry);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return dlist;
    }

    /***
     * 邀请用户参与主题
     * @param useIds
     */
    public void askUserJoin(long topicId, long[] useIds, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/threads/" + topicId + "/invite";
        JSONObject ob = new JSONObject();
        if (useIds.length > 0) {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < useIds.length; i++) {
                arr.put(useIds[i]);
            }
            try {
                ob.put("result", arr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new DataRequest<ArrayList<ColumnEntry>>(mContext) {
            @Override
            public ArrayList<ColumnEntry> jsonToBean(JSONObject json) {
                ArrayList<ColumnEntry> dlist = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<ColumnEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ColumnEntry entry = g.fromJson(object.toString(), ColumnEntry.class);
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
                                setRequestType(DataReuqestType.POST).
                                setJsonParams(ob).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                callback);
    }


    public IGetToken getTokenHelper() {
        IGetToken tokenHelper = GetTokenManager.getInstance().getIGetToken();
        return tokenHelper;
    }

    /**
     * 批量获取圈子详情列表
     *
     * @param ids
     * @return
     */
    public ArrayList<TopicalEntry> getRealDataByIds(boolean isHome, Long... ids) {
        String url = severUrl +
                "/public/threads/multi/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(), null);
            try {
                StringUtil.checkHttpResponseError(res);
                JSONArray array = new JSONArray(res);
                if (array != null) {
                    Gson gson = new Gson();
                    final ArrayList<TopicalEntry> adlist = new ArrayList<>();
                    for (int k = 0; k < array.length(); k++) {
                        JSONObject json = array.optJSONObject(k);
                        TopicalEntry topicalEntry = null;
                        if (json != null) {
                            topicalEntry = new Gson().fromJson(json.toString(), TopicalEntry.class);
                            if (topicalEntry != null) {
                                if (App.getInstance().getUser() != null) {
                                    if (topicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                                        int s = isAttentionOther(topicalEntry.getAuthor_id());
                                        topicalEntry.setRelationRole(s);
                                    } else
                                        topicalEntry.setRelationRole(-1);
                                } else
                                    topicalEntry.setRelationRole(3);
                                boolean isFavl = isFav(topicalEntry.getId());
                                topicalEntry.setIsFavl(isFavl);
                                topicalEntry.setHome(isHome);
                                if (topicalEntry.getAttachmentInfos() == null) {
                                    String respone = getSyncAtthmentById(topicalEntry.getAttachments());
                                    if (!TextUtils.isEmpty(respone.toString().trim())) {
                                        JSONObject attjson = JsonCreater.jsonParseString(respone);
                                        JSONArray arr = attjson.optJSONArray("result");
                                        Map<Long, Attachment> map = null;
                                        if (arr != null && arr.length() > 0) {
                                            map = new LinkedHashMap<>();
                                            List<Attachment> list = new ArrayList<Attachment>();
                                            String urls = "";
                                            int screenWidth = UtilHelp.getScreenWidth(mContext);
                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject obj = (JSONObject) arr.get(i);
                                                Attachment cp = gson.fromJson(obj.toString(),
                                                        Attachment.class);
                                                if (i == 0)
                                                    topicalEntry.setType(cp.getType());
                                                if (cp.getType() == 1) {
                                                    urls += cp.getUrl() + "?w=" + screenWidth + "&h=0&s=1";
//                                                    urls += cp.getUrl();
                                                    if (i != arr.length() - 1)
                                                        urls += ',';
                                                    topicalEntry.setUrls(urls);
                                                }
                                                if (cp != null)
                                                    map.put(cp.getId(), cp);
                                            }
                                            List<Long> idss = topicalEntry.getAttachments();
                                            if (!(map == null || map.isEmpty()) &&
                                                    !(idss == null || idss.isEmpty())) {
                                                for (int i = 0; i < idss.size(); i++) {
                                                    Attachment ap = map.get(idss.get(i));
                                                    if (ap != null)
                                                        list.add(ap);
                                                }
                                            }
                                            topicalEntry.setAttachmentss(list);
                                        }
                                    }
                                }
                                //获取用户等级
                                Level level = UserLevelManager.getInstance().findLevelSync(mContext, topicalEntry.getAuthor_id());
                                if (level != null) {
                                    topicalEntry.setUser_level_img(level.getIconUrl());
                                }
                                //浏览人数
                                if (topicalEntry.getVisitList() == null) {
                                    List<TopicalEntry.VisitorBean> list = getCMYVisitors(topicalEntry.getId());
                                    topicalEntry.setVisitList(list);
//                                    JSONArray arr = json.optJSONArray("recent_viewers");
//                                    List<TopicalEntry.VisitorBean> Visitorlist = null;
//                                    if (arr != null && arr.length() > 0) {
//                                        Visitorlist = new ArrayList<TopicalEntry.VisitorBean>();
//                                        for (int i = 0; i < arr.length(); i++) {
//                                            JSONObject obj = (JSONObject) arr.get(i);
//                                            TopicalEntry.VisitorBean cp = gson.fromJson(obj.toString(),
//                                                    TopicalEntry.VisitorBean.class);
//                                            Visitorlist.add(cp);
//                                        }
//                                        topicalEntry.setVisitList(Visitorlist);
//                                    }
                                }
                            }
                        }
//                        TopicalEntry data = gson.fromJson(array.optJSONObject(i).toString(),
//                                TopicalEntry.class);
//                        TopicalEntry tag = getTopicTopicalInfo(data);
                        adlist.add(topicalEntry);
                    }
                    return adlist;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 同步获取主题的其他参数  比如 关注  收藏  用户等级
     *
     * @param sTopicalEntry
     * @return
     */
    public TopicalEntry getTopicParams(TopicalEntry sTopicalEntry, boolean isHome) {
        Gson gson = new Gson();
        try {
            if (sTopicalEntry != null) {
                if (App.getInstance().getUser() != null) {
                    if (sTopicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                        int res = isAttentionOther(sTopicalEntry.getAuthor_id());
                        sTopicalEntry.setRelationRole(res);
                    } else
                        sTopicalEntry.setRelationRole(-1);   // 隐藏关注
                } else
                    sTopicalEntry.setRelationRole(3);   // 显示关注
                boolean isFavl = isFav(sTopicalEntry.getId());
                sTopicalEntry.setIsFavl(isFavl);
                sTopicalEntry.setHome(isHome);
                if (sTopicalEntry.getAttachmentInfos() == null) {
                    String respone = getSyncAtthmentById(sTopicalEntry.getAttachments());
                    if (!TextUtils.isEmpty(respone.toString().trim())) {
                        JSONObject attjson = JsonCreater.jsonParseString(respone);
                        JSONArray arr = attjson.optJSONArray("result");
                        Map<Long, Attachment> map = null;
                        if (arr != null && arr.length() > 0) {
                            map = new LinkedHashMap<>();
                            List<Attachment> list = new ArrayList<Attachment>();
                            String urls = "";
                            int screenWidth = UtilHelp.getScreenWidth(mContext);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                Attachment cp = gson.fromJson(obj.toString(),
                                        Attachment.class);
                                if (i == 0)
                                    sTopicalEntry.setType(cp.getType());
                                if (cp.getType() == 1) {
                                    urls += cp.getUrl() + "?w=" + screenWidth + "&h=0&s=1";
//                                                    urls += cp.getUrl();
                                    if (i != arr.length() - 1)
                                        urls += ',';
                                    sTopicalEntry.setUrls(urls);
                                }
                                if (cp != null)
                                    map.put(cp.getId(), cp);
                            }
                            List<Long> idss = sTopicalEntry.getAttachments();
                            if (!(map == null || map.isEmpty()) &&
                                    !(idss == null || idss.isEmpty())) {
                                for (int i = 0; i < idss.size(); i++) {
                                    Attachment ap = map.get(idss.get(i));
                                    if (ap != null)
                                        list.add(ap);
                                }
                            }
                            sTopicalEntry.setAttachmentss(list);
                        }
                    }
                }
                //获取用户等级
                Level level = UserLevelManager.getInstance().findLevelSync(mContext, sTopicalEntry.getAuthor_id());
                if (level != null) {
                    sTopicalEntry.setUser_level_img(level.getIconUrl());
                }
                //浏览人数
                if (sTopicalEntry.getVisitList() == null) {
                    List<TopicalEntry.VisitorBean> list = getCMYVisitors(sTopicalEntry.getId());
                    sTopicalEntry.setVisitList(list);
                }
            }
        } catch (ApiException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return sTopicalEntry;
    }


    /**
     * 同步获取主题的详情
     *
     * @param sTopicalEntry
     * @return
     */
    /**
     * 同步获取主题的详情
     *
     * @param sTopicalEntry
     * @return
     */
    public TopicalEntry getTopicTopicalInfo(TopicalEntry sTopicalEntry) {
        String url = severUrl + "/public/threads/" + sTopicalEntry.getId();
        TopicalEntry topicalEntry = null;
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        JSONObject json = null;
        Gson gson = new Gson();
        try {
            json = JsonCreater.jsonParseString(response);
            String token = UtilHelp.checkTokenError(json);  //   检查Token是否有效
            if (token != null) {
                if (!TextUtils.isEmpty(token)) {
                    Account account = App.getInstance().getUser();
                    account.setToken(token);
                    CoreApp.getInstance().setCurrentAccount(account);
                    getTopicTopicalInfo(sTopicalEntry);
                    return null;
                }
            }
            if (json != null) {
                topicalEntry = new Gson().fromJson(json.toString(), TopicalEntry.class);
                if (topicalEntry != null) {
                    topicalEntry.setType(sTopicalEntry.getType());
                    if (App.getInstance().getUser() != null) {
                        if (sTopicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
                            int res = isAttentionOther(sTopicalEntry.getAuthor_id());
                            topicalEntry.setRelationRole(res);
                        } else
                            topicalEntry.setRelationRole(-1);   // 隐藏关注
                    } else
                        topicalEntry.setRelationRole(3);   // 显示关注
                    boolean isFavl = isFav(sTopicalEntry.getId());
                    topicalEntry.setIsFavl(isFavl);
                    topicalEntry.setHome(sTopicalEntry.isHome());
                    if (topicalEntry.getAttachmentInfos() == null) {
                        List<Long> attids = topicalEntry.getAttachments();
                        getSynchroAttments(attids, topicalEntry);
                    }
                    //获取用户等级
                    Level level = UserLevelManager.getInstance().findLevelSync(mContext, topicalEntry.getAuthor_id());
                    if (level != null) {
                        topicalEntry.setUser_level_img(level.getIconUrl());
                    }
                    //浏览人数
                    if (topicalEntry.getVisitList() == null) {
                        JSONArray arr = json.optJSONArray("recent_viewers");
                        List<TopicalEntry.VisitorBean> Visitorlist = null;
                        if (arr != null && arr.length() > 0) {
                            Visitorlist = new ArrayList<TopicalEntry.VisitorBean>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                TopicalEntry.VisitorBean cp = gson.fromJson(obj.toString(),
                                        TopicalEntry.VisitorBean.class);
                                Visitorlist.add(cp);
                            }
                            topicalEntry.setVisitList(Visitorlist);
                        }
                    }

                    //點讚
                    if (topicalEntry.getPraiseList() == null) {
                        List<TopicalEntry.PraiseBean> tp = null;
                        JSONArray arr = json.optJSONArray("recent_attitudes");
                        if (!(arr == null || arr.length() == 0)) {
                            tp = new ArrayList<>();
                            Gson gs = new Gson();
                            for (int k = 0; k < arr.length(); k++) {
                                JSONObject pb = (JSONObject) arr.get(k);
                                TopicalEntry.PraiseBean prab = gs.fromJson(pb.toString(), TopicalEntry.PraiseBean.class);
                                if (prab.getState() != 1) continue;
                                tp.add(prab);
                            }
                            topicalEntry.setPraiseBeanList(tp);
                        }
                    }

                    if (topicalEntry.getReplyList() == null) {
                        List<ReplyEntry> tp = null;
                        JSONArray arr = json.optJSONArray("recent_replies");
                        if (!(arr == null || arr.length() == 0)) {
                            tp = new ArrayList<>();
                            Gson gs = new Gson();
                            for (int k = 0; k < arr.length(); k++) {
                                JSONObject pb = (JSONObject) arr.get(k);
                                ReplyEntry prab = gs.fromJson(pb.toString(), ReplyEntry.class);
                                tp.add(prab);
                            }
                            topicalEntry.setReplyList(tp);
                        }
                    }
                }
            }
        } catch (ApiException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return topicalEntry;
    }

    /*
*  同步批量获取附件
*
* @param attids
* @param entry
*/
    public void getSynchroAttments(List<Long> attids, TopicalEntry entry) {
        if (!(attids == null || attids.isEmpty())) {
            List<Attachment> lists = new ArrayList<>();
            if (!(attids == null || attids.isEmpty())) {
                Map<Long, Attachment> map = getAttinfos(attids.toArray(new Long[0]));
                if (!(map == null || map.isEmpty())) {
                    String urls = "";
                    for (int k = 0; k < attids.size(); k++) {
                        Attachment mt = map.get(attids.get(k));
                        if (mt == null) continue;
                        if (mt.getType() == 1 || mt.getType() == 4) {
//                                        urls += mt.getUrl() + "?w=" + screenWidth + "&h=" + screenHeight + "&s=0";
                            urls += mt.getUrl();
                            if (k != attids.size() - 1)
                                urls += ',';
                        }
                        lists.add(mt);
                    }
                    entry.setUrls(urls);
                }
            }
            entry.setAttachmentss(lists);
            entry.setType(getQuanziType(lists));
        }
    }

    /*
*  得到圈子的实际type  视频还是图片
* @param att
* @return
*/
    public int getQuanziType(List<Attachment> list) {
        int type = 1;
        if (!(list == null || list.isEmpty())) {
            Attachment att = list.get(0);
            if (att != null) {
                if (att.getType() == 1) {
                    type = 1;
                } else if (att.getType() == 2) {
                    type = 2;//video
                } else {
                    String path = att.getUrl();
                    if (!TextUtils.isEmpty(path)) {
                        String pix = FileUtil.getSuffix(path.toLowerCase().toString());
                        if (TextUtils.equals(pix, ".mp4") ||
                                TextUtils.equals(pix, ".flv") ||
                                TextUtils.equals(pix, ".m3u8")) {
                            type = 2;//video
                        }
                    }
                }
            }
        }
        return type;
    }

    /**
     * 获取点赞列表
     *
     * @param id
     * @return
     */
    public String getPraiseNumberList(long id) {
        String appendString = "";
        String url = severUrl + "/public/threads/" + id + "/attitudes?state=1&max=100";
        try {
            String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            JSONObject jsonObject = JsonCreater.jsonParseString(response);
            if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                JSONArray result = jsonObject.optJSONArray("result");
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = (JSONObject) result.get(i);
//                        ContentCmsEntry entry = new Gson().fromJson(item.toString(), ContentCmsEntry.class);
                        appendString += item.optString("nickname");
                        if (i != result.length() - 1)
                            appendString += " • ";
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
     * 删除自己的主题
     *
     * @param id
     * @param callback
     */
    public void delTopticById(long id, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/users/current/threads/" + id;
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setRequestType(DataReuqestType.DEL).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                callback);
    }

    /*
     * 同步获取主题的详情
     *
     * @param id
     * @return
     */
    public TopicalEntry getTopicAllColumns(long id) {
        String url = severUrl + "/public/columns/" + id;
        TopicalEntry topicalEntry = null;
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        try {
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                topicalEntry = new Gson().fromJson(json.toString(), TopicalEntry.class);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return topicalEntry;
    }

    /*
    *   获取主题的附件
     */
    public void getTopicAttachs(long tId, DataRequest.DataCallback callback) {
        String url = App.getInstance().getBaseUrl() + "/public/threads/" + tId;
        new DataRequest<String>(mContext) {
            @Override
            public String jsonToBean(JSONObject json) {
                String result = "";
                if (json != null && !TextUtils.isEmpty(json.toString())) {
//                        result=json.optJSONArray("attachments");
                }
                return result;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    public void attentionAuthor(long uId, int isFoucus, DataRequest.DataCallback callback) {
        String url = App.getInstance().getPotrtServerUrl() + "/public/users/current/follow/" + uId;
        boolean flag = isFoucus == 0 || isFoucus == 3 ? true : false;
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
                                setBooleanParams(flag).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 关注/取消某人
     */
    public boolean attentionSyantAuthor(long uId, int isFoucus) {
        String url = App.getInstance().getPotrtServerUrl() + "/public/users/current/follow/" + uId;
        boolean flag = isFoucus == 0 ? true : false;
        JSONObject json = JsonHelper.httpPostJson(url, new JSONObject(), App.getInstance().getCurrentToken());
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            flag = true;
        }
        return flag;
    }

    /**
     * 发表主题
     */
    public void publisTopic(long cId, final String content, ArrayList<MediaModel> imgs, final List<String> tags, final ProgessCallbacface callbacface, Observer<Long> op) {
        final String url = severUrl + "/public/columns/" + cId + "/threads";
        Observable.just(imgs).
                observeOn(Schedulers.io()).
                map(new Func1<ArrayList<MediaModel>, Long>() {
                    @Override
                    public Long call(ArrayList<MediaModel> list) {
                        long topicId = -1;
                        JSONArray arr = new JSONArray();
                        if (list != null && !list.isEmpty()) {
                            String upFIleUrl = "";
                            try {
                                upFIleUrl = getImaUpfileUrl();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (TextUtils.isEmpty(upFIleUrl)) {
                                try {
                                    throw new ApiException("获取图片上传地址失败!");
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (list != null && !list.isEmpty()) {
                                for (int i = 0; i < list.size(); i++) {
                                    MediaModel model = list.get(i);
                                    String sharPath = synchronNewUpfile(upFIleUrl, model.url);
                                    if (!TextUtils.isEmpty(sharPath)) {
                                        callbacface.MyObtainProgressValues(i * 20);
                                        try {
                                            JSONObject ob = new JSONObject();
                                            JSONArray yy = new JSONArray();
//                                        yy.put(sharPath + "//" + model.name);
                                            yy.put(sharPath);
//                                        ob.put(model.name, sharPath + "\\" + model.name);
                                            ob.put("name", model.name);
                                            ob.put("paths", yy);
                                            arr.put(ob);
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        JSONArray tagsArr = new JSONArray();
                        if (tags != null && tags.size() > 0) {
                            for (String str : tags) {
                                tagsArr.put(str);
                            }
                        }
                        JSONObject parmaObj = new JSONObject();
                        try {
                            parmaObj.put("title", "");
                            parmaObj.put("content", content);
                            parmaObj.put("attachment_infos", arr);
                            parmaObj.put("tags", tagsArr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject result = JsonHelper.httpPostJson(url, parmaObj, App.getInstance().getCurrentToken());
                        if (result != null && !TextUtils.isEmpty(result.toString())) {
                            topicId = result.optLong("id");
                        }
                        return topicId;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(op);
    }

    /*
  *   获取分类下所有主题
  */
    public void getTopiccalList(long columnId, int page, DataRequest.DataCallback callback, final Observer<List<TopicalEntry>> back) {
        String url = severUrl + "/public/columns/" + columnId + "/threads?page=" + page;
        new DataRequest<ArrayList<TopicalEntry>>(mContext) {
            @Override
            public ArrayList<TopicalEntry> jsonToBean(JSONObject json) {
                ArrayList<TopicalEntry> dlist = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<TopicalEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                TopicalEntry entry = g.fromJson(object.toString(), TopicalEntry.class);
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
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                new DataRequest.DataCallback<ArrayList<TopicalEntry>>() {
                    @Override
                    public void onSuccess(boolean isAppend, ArrayList<TopicalEntry> topicalies) {
                        Observable.from(topicalies)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<TopicalEntry, TopicalEntry>() {
                                    @Override
                                    public TopicalEntry call(TopicalEntry topicalEntry) {
                                        List<Attachment> list = new ArrayList<Attachment>();
                                        try {
                                            String respone = getSyncAtthmentById(topicalEntry.getAttachments());
                                            JSONObject json = JsonCreater.jsonParseString(respone);
                                            Gson gson = new Gson();
                                            Attachment cp = gson.fromJson(json.toString(),
                                                    Attachment.class);
                                            list.add(cp);
                                            topicalEntry.setAttachmentss(list);
                                        } catch (ApiException e) {
                                            e.printStackTrace();
                                        }
                                        return topicalEntry;
                                    }
                                })
                                .toList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(back);
                    }

                    @Override
                    public void onFail(ApiException e) {

                    }
                });
    }

    public void addViewCount(long topId) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topId + "/view";
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
                                setJsonParams(new JSONObject()).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(new DataRequest.DataCallback() {
            @Override
            public void onSuccess(boolean isAppend, Object data) {

            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 收藏 / 取消收藏 主题
     */
    public void farityToptic(long topicalId, boolean flag, DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topicalId + "/favor";
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
                                setBooleanParams(flag).
                                setRequestType(DataReuqestType.POST).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 判断是否被收藏
     *
     * @param topId
     * @return
     */
    public boolean isFav(long topId) {
        boolean flag = false;
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/favored/" + topId;
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        try {
            JSONObject json = JsonCreater.jsonParseString(response);
            if (json != null) {
                flag = json.optBoolean(String.valueOf(topId));
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取主题的浏览人数
     *
     * @param topId
     * @return
     */
    public List<TopicalEntry.VisitorBean> getCMYVisitors(long topId) {
        List<TopicalEntry.VisitorBean> dlist = null;
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topId + "/viewers?max=6";
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        try {
            JSONObject json = JsonCreater.jsonParseString(response);
            JSONArray arr = json.optJSONArray("result");
            if (arr != null && arr.length() > 0) {
                dlist = new ArrayList<TopicalEntry.VisitorBean>();
                Gson g = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject ob = (JSONObject) arr.get(i);
                    if (ob == null) continue;
                    TopicalEntry.VisitorBean bean = g.fromJson(ob.toString(), TopicalEntry.VisitorBean.class);
                    dlist.add(bean);
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
     * 我是否关注其他用户
     */
    public int isAttentionOther(long uId) {
        int result = 0;
        String url = App.getInstance().getmSession().getPortalServerUrl() + "/public/users/current/followed/" + uId;
        String response = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        try {
            JSONObject josn = JsonCreater.jsonParseString(response);
            JSONObject ob = josn.optJSONObject(String.valueOf(uId));
            if (ob == null) return result;
            boolean followed = ob.optBoolean("followed");
            boolean fanned = ob.optBoolean("fanned");
            if (followed) result = 1;  //已关注
            if (followed && fanned) result = 1;  //相互关注
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 同步获取多个附件信息
     *
     * @param ids
     * @return
     */
    public String getSyncAtthmentById(List<Long> ids) {
        String str = "";
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/attachments/";
        if (ids != null && !ids.isEmpty()) {
            for (int i = 0; i < ids.size(); i++) {
                url += ids.get(i);
                if (i != ids.size() - 1)
                    url += ",";
            }
            str = HttpUtil.executeGet(url, new HttpParameters(), null);
        }
        return str;
    }


    /**
     * 同步获取单个附件信息
     *
     * @param ids
     * @return
     */
    public String getAtthmentById(long ids) {
        String str = "";
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/attachments/" + ids;
        str = HttpUtil.executeGet(url, new HttpParameters(), null);
        return str;
    }

    /**
     * 获取主题回复列表 (暂定)
     */
    public void getTopicReplayList(long tId, int page, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/threads/" + tId + "/replies?page=" + page;
        new DataRequest<ArrayList<ReplyEntry>>(mContext) {
            @Override
            public ArrayList<ReplyEntry> jsonToBean(JSONObject json) {
                ArrayList<ReplyEntry> dlist = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        dlist = new ArrayList<ReplyEntry>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject object = (JSONObject) arr.get(i);
                                ReplyEntry entry = g.fromJson(object.toString(), ReplyEntry.class);
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
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                callback);
    }

    /**
     * 根据附件ID 获取附件的详细信息
     */
    public void getAttachmentInfo(ImageView atgImg, long id) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/attachments/" + id;
        new DataRequest<Attachment>(mContext) {
            @Override
            public Attachment jsonToBean(JSONObject json) {
                Attachment entry = null;
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    try {
                        JSONArray arr = json.optJSONArray("result");
                        if (arr != null && arr.length() > 0) {
                            Gson g = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject ob = null;
                                ob = (JSONObject) arr.get(i);
                                entry = g.fromJson(ob.toString(), Attachment.class);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return entry;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).
                                setTagView(atgImg).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(
                new DataRequest.DataCallbackTag() {
                    @Override
                    public void onSuccess(Object object, boolean isAppend, Object data) {
                        Attachment entry = (Attachment) data;
                        ImageView img = (ImageView) object;
                        Util.LoadThumebImage(img, entry.getUrl(), null);
                    }

                    @Override
                    public void onSuccess(boolean isAppend, Object data) {
                        int a = 0;
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 点赞
     */
    public void pariseToptic(long topicalId, DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topicalId + "/like";
        JSONObject obj = new JSONObject();
        try {
            obj.put("ip_address", App.getInstance().getLocalIp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                                setJsonParams(obj).
                                setRequestType(DataReuqestType.POST).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }


    /**
     * 取消点赞
     */
    public void cancelPariseToptic(long topicalId, DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topicalId + "/cancel-attitude";
        JSONObject obj = new JSONObject();
        try {
            obj.put("ip_address", App.getInstance().getLocalIp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                                setJsonParams(obj).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 点踩
     */
    public void strampToptic(long topicalId, DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topicalId + "/dislike";
        JSONObject obj = new JSONObject();
        try {
            obj.put("ip_address", App.getInstance().getLocalIp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                                setJsonParams(obj).
                                setToken(App.getInstance().getCurrentToken()).
                                build(),
                false).setCallback(callback);
    }

    /**
     * 取消点踩
     */
//    public void cancelStempToptic(long topicalId, DataRequest.DataCallback callback) {
//        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/threads/" + topicalId + "/cancel-attitude";
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("ip_address", App.getInstance().getLocalIp());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new DataRequest<Boolean>(mContext) {
//            @Override
//            public Boolean jsonToBean(JSONObject json) {
//                if (json != null && !TextUtils.isEmpty(json.toString())) {
//                    return true;
//                }
//                return false;
//            }
//        }.getData(new DataRequest.HttpParamsBuilder()
//                        .setUrl(url).
//                                setJsonParams(obj).
//                                setToken(App.getInstance().getCurrentToken()).
//                                build(),
//                false).setCallback(callback);
//    }

    /**
     * 发表回复  评论
     */
    public void createComment(long cId, long tId, String content, Attachment att, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/threads/" + cId + "/replies";
        JSONObject obj = new JSONObject();
        try {
            obj.put("ref_reply_id", tId);
            obj.put("content", content);
            JSONArray arr = new JSONArray();
            if (att != null) {
                JSONObject img = new JSONObject();
                img.put("name", att.getName());
                JSONArray uls = new JSONArray();
                uls.put(att.getUrl());
                img.put("paths", uls);
                arr.put(img);
            }
            obj.put("attachment_infos", arr);
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
     * 上传图片
     *
     * @param path
     * @return
     */
    public String synchronUpfile(String path) {
        String ralatPath = "";
        try {
            String response = HttpUtil.uploadFileSynchronized(pictureUrl, path, null);
            JSONObject json = null;
            try {
                json = JsonCreater.jsonParseString(response);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ralatPath;
    }

//    public String synchronNewUpfile(String upUrl, String path) {
//        String ralatPath = "";
//        try {
//            upUrl = UtilHelp.checkUrl(upUrl);
//            JSONObject json = JsonHelper.httpUpload(upUrl, path, null);
//            if (json != null) {
//                int isOk = json.optInt("isOK");
//                if (isOk == 1) {
//                    ralatPath = json.optString("relativepath");
//                    ralatPath += json.optString("name");
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ralatPath;
//    }

    public String synchronNewUpfile(String upUrl, String path) {
        String ralatPath = "";
        try {
            upUrl = UtilHelp.checkUrl(upUrl);

            String res = HttpUtil.uploadFileSynchronized(upUrl,
                    path, null);
            JSONObject json = new JSONObject(res);
//            JSONObject json = JsonHelper.httpUpload(upUrl, path, null);
            if (json != null) {
                int isOk = json.optInt("isOK");
                if (isOk == 1) {
                    ralatPath = json.optString("relativepath");
                    ralatPath += json.optString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ralatPath;
    }


    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * 批量获取是否關注其他用戶
     *
     * @param userIds
     * @return
     */
    public Map<Long, Boolean> getAttionUserFlagsByIds(Long... userIds) {
        Map<Long, Boolean> favMap = null;
        String url = App.getInstance().getmSession().getPortalServerUrl() +
                "/public/users/current/followed/";
        if (userIds != null) {
            for (int i = 0; i < userIds.length; i++) {
                String appendString = userIds[i] + "";
                if (i != userIds.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            //url="http://api.leshantv.net/public/users/current/followed/73,87,335,3808,15741517,11293661,143";
            String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            try {
                JSONObject obj = JsonCreater.jsonParseString(res);
                if (obj != null) {
                    int error = obj.optInt("error");
                    if (error == 401) {
                        String token = UtilHelp.checkTokenError(obj);
                        if (!TextUtils.isEmpty(token)) {
                            res = HttpUtil.executeGet(url, new HttpParameters(), token);
                            obj = JsonCreater.jsonParseString(res);
                        }
                    } else {
//                        StringUtil.checkHttpResponseError(res);
                    }
                }
                if (obj != null) {
                    favMap = new HashMap<>();
                    for (int i = 0; i < userIds.length; i++) {
                        JSONObject item = obj.optJSONObject(String.valueOf(userIds[i]));
                        if (item == null) continue;
                        boolean flag = item.optBoolean("followed");  //  treu 關注  false  未關注
                        favMap.put(userIds[i], flag);
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        return favMap;
    }

    /**
     * 批量获取圈子收藏
     *
     * @param ids
     * @return
     */
    public Map<Long, Boolean> getFavorityFlagsByIds(Long... ids) {
        Map<Long, Boolean> favMap = null;
        String url = severUrl +
                "/public/threads/favored/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            try {
                JSONObject obj = JsonCreater.jsonParseString(res);
                if (obj != null) {
                    int error = obj.optInt("error");
                    if (error == 401) {
                        String token = UtilHelp.checkTokenError(obj);
                        if (!TextUtils.isEmpty(token)) {
                            res = HttpUtil.executeGet(url, new HttpParameters(), token);
                            obj = JsonCreater.jsonParseString(res);
                        }
                    } else {
//                        StringUtil.checkHttpResponseError(res);
                    }
                }
                if (obj != null) {
                    favMap = new HashMap<>();
                    for (int i = 0; i < ids.length; i++) {
                        boolean bfalg = obj.optBoolean(String.valueOf(ids[i]));
                        favMap.put(ids[i], bfalg);
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        return favMap;
    }


    /**
     * 批量獲取用戶的等級
     *
     * @param ids
     * @return
     */
    public Map<Long, Level> getUserLvelByIds(Long... ids) {
        Map<Long, Level> favMap = null;
        String url = App.getInstance().getmSession().getPortalServerUrl() +
                "/public/user-levels/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            try {
                JSONObject obj = JsonCreater.jsonParseString(res);
                if (obj != null) {
                    int error = obj.optInt("error");
                    if (error == 401) {
                        String token = UtilHelp.checkTokenError(obj);
                        if (!TextUtils.isEmpty(token)) {
                            res = HttpUtil.executeGet(url, new HttpParameters(), token);
                            obj = JsonCreater.jsonParseString(res);
                        }
                    } else {
//                        StringUtil.checkHttpResponseError(res);
                    }
                }
                if (obj != null) {
                    favMap = new HashMap<>();
                    Gson gs = new Gson();
                    JSONArray arr = obj.optJSONArray("result");
                    if (!(arr == null || arr.length() == 0)) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject itenm = (JSONObject) arr.get(i);
                            Level level = gs.fromJson(itenm.toString(), Level.class);
                            if (level == null) continue;
                            favMap.put(level.getId(), level);
                        }
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return favMap;
    }


    /**
     * 批量獲取圈子的附件信息
     *
     * @param dlist
     * @return
     */
//    public Map<Long, List<Attachment>> getCmyAttachmensByIds(ArrayList<TopicalEntry> dlist) {
//        Map<Long, List<Attachment>> favMap = null;
//        if (!(dlist == null || dlist.isEmpty())) {
//            favMap = new HashMap<>();
//            for (int i = 0; i < dlist.size(); i++) {
//                TopicalEntry item = dlist.get(i);
//                if (item == null) continue;
//                if (!(item.getAttachments() == null || item.getAttachments().isEmpty())) {
//                    ArrayList<Long> attList = new ArrayList<>();
//                    for (Long id : item.getAttachments()) {
//                        attList.add(id);
//                    }
//                    if (!(attList == null || attList.isEmpty())) {
//                        List<Attachment> ds = new ArrayList<>();
//                        Map<Long, Attachment> map = getAttinfos(attList.toArray(new Long[0]));
//                        if (!(map == null || map.isEmpty())) {
//                            for (int k = 0; k < attList.size(); k++) {
//                                Attachment mt = map.get(attList.get(k));
//                                if (mt == null) continue;
//                                ds.add(mt);
//                            }
//                            favMap.put(item.getId(), ds);
//                        }
//                    }
//                }
//            }
//        }
//        return favMap;
//    }
    public Map<Long, List<Attachment>> getCmyAttachmensByIds(ArrayList<TopicalEntry> dlist) {
        Map<Long, List<Long>> childIdMaps = null;
        Map<Long, List<Attachment>> AttsMap = null;
        ArrayList<Long> attList = new ArrayList<>();
        if (!(dlist == null || dlist.isEmpty())) {
            childIdMaps = new HashMap<>();
            for (int i = 0; i < dlist.size(); i++) {
                TopicalEntry item = dlist.get(i);
                if (item == null) continue;
                if (!(item.getAttachments() == null || item.getAttachments().isEmpty())) {
                    ArrayList<Long> child = new ArrayList<>();
                    for (Long id : item.getAttachments()) {
                        attList.add(id);
                        child.add(id);
                    }
                    childIdMaps.put(item.getId(), child);
                }
            }
            if (!(attList == null || attList.isEmpty())) {
                Map<Long, Attachment> map = getAttinfos(attList.toArray(new Long[0]));
                if (!(map == null || map.isEmpty())) {
                    AttsMap = new HashMap<>();
                    Iterator<Map.Entry<Long, List<Long>>> it = childIdMaps.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Long, List<Long>> entry = it.next();
                        if (!(entry.getValue() == null || entry.getValue().isEmpty())) {
                            List<Attachment> ds = new ArrayList<>();
                            for (Long id : entry.getValue()) {
                                Attachment att = map.get(id);
                                if (att == null) continue;
                                ds.add(att);
                            }
                            AttsMap.put(entry.getKey(), ds);
                        }
                    }
                }
            }
        }
        return AttsMap;
    }

    /**
     * 批量获取圈子的附件
     *
     * @param ids
     * @return
     */
    public Map<Long, Attachment> getAttinfos(Long... ids) {
        Map<Long, Attachment> dlist = null;
        String url = severUrl +
                "/public/attachments/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
            try {
                JSONObject obj = JsonCreater.jsonParseString(res);
                if (obj != null) {
                    int error = obj.optInt("error");
                    if (error == 401) {
                        String token = UtilHelp.checkTokenError(obj);
                        if (!TextUtils.isEmpty(token)) {
                            res = HttpUtil.executeGet(url, new HttpParameters(), token);
                            obj = JsonCreater.jsonParseString(res);
                        }
                    } else {
//                        StringUtil.checkHttpResponseError(res);
                    }
                }
                if (obj != null) {
                    JSONArray arr = obj.optJSONArray("result");
                    if (!(arr == null || arr.length() == 0)) {
                        Gson gs = new Gson();
                        dlist = new HashMap<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject itenm = (JSONObject) arr.get(i);
                            if (itenm == null) continue;
                            Attachment ach = gs.fromJson(itenm.toString(), Attachment.class);
                            if (ach == null) continue;
                            dlist.put(ach.getId(), ach);
                        }
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dlist;
    }

    /**
     * 获取圈子附件信息
     *
     * @param sTopicalEntry
     * @return
     */

    public TopicalEntry getTopicInfoParams(TopicalEntry sTopicalEntry) {
        Gson gson = new Gson();
        try {
            if (sTopicalEntry != null) {
//                if (App.getInstance().getUser() != null) {
//                    if (sTopicalEntry.getAuthor_id() != App.getInstance().getUser().getUser().getId()) {
//                        int res = isAttentionOther(sTopicalEntry.getAuthor_id());
//                        sTopicalEntry.setRelationRole(res);
//                    } else
//                        sTopicalEntry.setRelationRole(-1);   // 隐藏关注
//                } else
//                    sTopicalEntry.setRelationRole(3);   // 显示关注
//                boolean isFavl = isFav(sTopicalEntry.getId());
//                sTopicalEntry.setIsFavl(isFavl);
//                sTopicalEntry.setHome(isHome);
                if (sTopicalEntry.getAttachmentInfos() == null) {
                    String respone = getSyncAtthmentById(sTopicalEntry.getAttachments());
                    if (!TextUtils.isEmpty(respone.toString().trim())) {
                        JSONObject attjson = JsonCreater.jsonParseString(respone);
                        JSONArray arr = attjson.optJSONArray("result");
                        Map<Long, Attachment> map = null;
                        if (arr != null && arr.length() > 0) {
                            map = new LinkedHashMap<>();
                            List<Attachment> list = new ArrayList<Attachment>();
                            String urls = "";
                            int screenWidth = UtilHelp.getScreenWidth(mContext);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                Attachment cp = gson.fromJson(obj.toString(),
                                        Attachment.class);
                                if (i == 0)
                                    sTopicalEntry.setType(cp.getType());
                                if (cp.getType() == 1) {
                                    urls += cp.getUrl() + "?w=" + screenWidth + "&h=0&s=1";
//                                                    urls += cp.getUrl();
                                    if (i != arr.length() - 1)
                                        urls += ',';
                                    sTopicalEntry.setUrls(urls);
                                }
                                if (cp != null)
                                    map.put(cp.getId(), cp);
                            }
                            List<Long> idss = sTopicalEntry.getAttachments();
                            if (!(map == null || map.isEmpty()) &&
                                    !(idss == null || idss.isEmpty())) {
                                for (int i = 0; i < idss.size(); i++) {
                                    Attachment ap = map.get(idss.get(i));
                                    if (ap != null)
                                        list.add(ap);
                                }
                            }
                            sTopicalEntry.setAttachmentss(list);
                        }
                    }
                }
                //获取用户等级
//                Level level = UserLevelManager.getInstance().findLevelSync(mContext, sTopicalEntry.getAuthor_id());
//                if (level != null) {
//                    sTopicalEntry.setUser_level_img(level.getIconUrl());
//                }
                //浏览人数
//                if (sTopicalEntry.getVisitList() == null) {
//                    List<TopicalEntry.VisitorBean> list = getCMYVisitors(sTopicalEntry.getId());
//                    sTopicalEntry.setVisitList(list);
//                }
                //  点赞
//                if (sTopicalEntry.getPraiseList() == null) {
//                    List<TopicalEntry.PraiseBean> list = getPraiseList(sTopicalEntry.getId());
//                    sTopicalEntry.setPraiseBeanList(list);
//                }
                //子回复
//                if (sTopicalEntry.getReplyList() == null) {
//                    List<ReplyEntry> list = getReplyList(sTopicalEntry.getId());
//                    sTopicalEntry.setReplyList(list);
//                }
            }
        } catch (ApiException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return sTopicalEntry;
    }

}
