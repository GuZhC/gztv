package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.util.Log;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.ganzcms.app.model.ReplyEntry;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by heyang on 2016/11/2
 */
public class ReplyListManager extends DataFileCacheManager<ArrayList<ReplyEntry>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String REPLYLISTMANAGER = "ReplyListManager.tx";
    private AppSession mSession;
    private long mColumnType;


    public ReplyListManager(Context context, String accountId, long columnType) {
        super(context, accountId, REPLYLISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public ReplyListManager(Context context, String accountId, int columnType, boolean isLooper) {
        super(context, accountId, REPLYLISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public void setColumnType(int columnType) {
        this.mColumnType = columnType;
    }

    public void start(long tId, int page, boolean bAddTail) {
//        String param = "/public/threads/" + tId + "/replies?";   //  老版本
        String param = "/public/threads/" + tId + "/root-replies?";
        getDocObjectParam(bAddTail, param, page);
    }

    private void getDocObjectParam(boolean bAddTail, String param, long offset) {
        String url = mSession.getCommunityServerUrl();
        url += param;
        url += "page=" + offset + "&";
        url += "size=" + 20;
        Log.e("TAG", "http url == " + url);
        getData(new HttpParamsBuilder().
                setUrl(url).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail);
    }

    @Override
    public ArrayList<ReplyEntry> jsonToBean(JSONObject jsonObject) {
        ArrayList<ReplyEntry> dlist = null;
        Map<Long, ReplyEntry> lists = null;
        try {
            JSONArray result = jsonObject.optJSONArray("data");
            if (result != null) {
//                lists = new HashMap<Long, ReplyEntry>();
                dlist = new ArrayList<ReplyEntry>();
                for (int i = result.length() - 1; i >= 0; i--) {
                    JSONObject item = (JSONObject) result.get(i);
                    ReplyEntry entry = new Gson().fromJson(item.toString(), ReplyEntry.class);
                    JSONArray att = item.optJSONArray("attachments");
                    if (att != null && att.length() > 0) {
                        long id = (long) att.getLong(0);
                        entry.setAttmentId(id);
                    }

//                    if (entry.getRef_replies() != null && !entry.getRef_replies().isEmpty()) {
//                        for (ReplyEntry.RefRepliesBean bean : entry.getRef_replies()) {
//                            long id = bean.getId();
//                            ReplyEntry rp = lists.get(id);
//                            if (rp != null) {
//                                ReplyEntry.RefRepliesBean bs = new ReplyEntry.RefRepliesBean();
//                                bs.setAuthor_name(entry.getAuthor_name());
//                                bs.setAuthor_nickname(entry.getAuthor_nickname());
//                                bs.setContent(entry.getContent());
//                                List<ReplyEntry.RefRepliesBean> rpss = rp.getRef_replies();
//                                if (rpss == null) rpss = new ArrayList<ReplyEntry.RefRepliesBean>();
//                                rpss.add(bs);
//                                rp.setRef_replies(rpss);
//                            }
//                        }
//                    } else
//                        lists.put(entry.getId(), entry);
                    dlist.add(entry);
                }
//                if (lists != null && !lists.isEmpty()) {
//                    dlist = new ArrayList<ReplyEntry>();
//                    for (Long obj : lists.keySet()) {
//                        ReplyEntry value = lists.get(obj);
//                        dlist.add(value);
//                    }
//                    lists.clear();
//                }

                //时间倒序
                if (dlist != null && dlist.size() > 0) {
                    Collections.sort(dlist, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return new Long(((ReplyEntry) o2).getPost_time()).compareTo(new Long(((ReplyEntry) o1).getPost_time()));
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }


//    @Override
//    public ArrayList<ReplyEntry> jsonToBean(JSONObject jsonObject) {
//        ArrayList<ReplyEntry> dlist = null;
//        Map<Long, ReplyEntry> lists = null;
//        try {
//            JSONArray result = jsonObject.optJSONArray("data");
//            if (result != null) {
//                lists = new HashMap<Long, ReplyEntry>();
//                for (int i = result.length() - 1; i >= 0; i--) {
//                    JSONObject item = (JSONObject) result.get(i);
//                    ReplyEntry entry = new Gson().fromJson(item.toString(), ReplyEntry.class);
//                    dlist.add(entry);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return dlist;
//    }
}
