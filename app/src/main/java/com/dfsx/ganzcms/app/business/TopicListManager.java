package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2016/10-24
 */
public class TopicListManager extends DataFileCacheManager<ArrayList<TopicalEntry>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String TOPICLISTMANAGER = "TOPICLISTMANAGER.tx";
    private AppSession mSession;
    private long mColumnType = -1;
    private String accountsId = "";


    public TopicListManager(Context context, String accountId, long columnType) {
        super(context, accountId, TOPICLISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
        this.accountsId = accountId;
    }

    public TopicListManager(Context context, String accountId, int columnType, boolean isLooper) {
        super(context, accountId, TOPICLISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public void setColumnType(int columnType) {
        this.mColumnType = columnType;
    }

    public void start(long columnId, boolean bAddTail, int offset) {
        String param = "/public";
        if (mColumnType == -1) {
            if (Integer.parseInt(accountsId) == -1) {
                param += "/user/current/threads?";
            } else {
                param += "/user/" + accountsId + "/threads?";
            }
        } else {
            param += "/columns/" + columnId + "/threads?";
        }
//        String param = "/public/columns/" + columnId + "/threads?";
        getDocObjectParam(bAddTail, param, offset);
    }

    private void getDocObjectParam(boolean bAddTail, String param, long offset) {
        String url = mSession.getCommunityServerUrl();
        url += param;
        url += "page=" + offset + "&";
        url += "size=" + 10+"&";
        url += "orderby=post_time";
        Log.e("TAG", "http url == " + url);
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail);
    }

    @Override
    public ArrayList<TopicalEntry> jsonToBean(JSONObject jsonObject) {
        ArrayList<TopicalEntry> dlist = null;
        if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
            JSONArray data = jsonObject.optJSONArray("data");
            if (data != null) {
                dlist = new ArrayList<TopicalEntry>();
                Gson gs = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) data.get(i);
                        TopicalEntry entry = new Gson().fromJson(object.toString(), TopicalEntry.class);
                        if(mColumnType==-1) entry.setHome(true);
                        //點讚
                        if (entry.getPraiseList() == null) {
                            List<TopicalEntry.PraiseBean> tp = null;
                            JSONArray arr = object.optJSONArray("recent_attitudes");
                            if (!(arr == null || arr.length() == 0)) {
                                tp = new ArrayList<>();
                                for (int k = 0; k < arr.length(); k++) {
                                    JSONObject pb = (JSONObject) arr.get(k);
                                    TopicalEntry.PraiseBean prab = gs.fromJson(pb.toString(), TopicalEntry.PraiseBean.class);
                                    if (prab.getState() != 1) continue;
                                    tp.add(prab);
                                }
                                entry.setPraiseBeanList(tp);
                            }
                        }
                        if (entry.getReplyList() == null) {
                            List<ReplyEntry> tp = null;
                            JSONArray arr = object.optJSONArray("recent_replies");
                            if (!(arr == null || arr.length() == 0)) {
                                tp = new ArrayList<>();
                                for (int k = 0; k < arr.length(); k++) {
                                    JSONObject pb = (JSONObject) arr.get(k);
                                    ReplyEntry prab = gs.fromJson(pb.toString(), ReplyEntry.class);
                                    tp.add(prab);
                                }
                                entry.setReplyList(tp);
                            }
                        }
                        if (entry.getVisitList() == null) {
                            List<TopicalEntry.VisitorBean> tp = null;
                            JSONArray arr = object.optJSONArray("recent_viewers");
                            if (!(arr == null || arr.length() == 0)) {
                                tp = new ArrayList<>();
                                for (int k = 0; k < arr.length(); k++) {
                                    JSONObject pb = (JSONObject) arr.get(k);
                                    TopicalEntry.VisitorBean prab = gs.fromJson(pb.toString(), TopicalEntry.VisitorBean.class);
                                    tp.add(prab);
                                }
                                entry.setVisitList(tp);
                            }
                        }
                        dlist.add(entry);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dlist;
    }

}


