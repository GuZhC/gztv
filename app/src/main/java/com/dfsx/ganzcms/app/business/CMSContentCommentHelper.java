package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ContentComment;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CMSContentCommentHelper implements ICMSContentComment {
    private Context context;

    public CMSContentCommentHelper(Context context) {
        this.context = context;
    }

    @Override
    public void getCommentList(long contentId, int page, int pageSize, DataRequest.DataCallback<List<ContentComment>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl()
                + "/public/contents/" + contentId + "/comments?page="
                + page + "&size=" + pageSize;

        new DataRequest<List<ContentComment>>(context) {
            @Override
            public List<ContentComment> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray array = json.optJSONArray("data");
                    if (array != null) {
                        ArrayList<ContentComment> list = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            ContentComment comment = gson.fromJson(array.optJSONObject(i).toString(),
                                    ContentComment.class);
                            list.add(comment);
                        }
                        return list;
                    }

                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .build(), page > 1)
                .setCallback(callback);
    }

    @Override
    public void getRootCommentList(long contentId, int page, int pageSize, int sub_comment_count, DataRequest.DataCallback<List<ContentComment>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl()
                + "/public/contents/" + contentId + "/root-comments?page=" + page +
                "&size=" + pageSize + "&sub_comment_count=" + sub_comment_count;
        new DataRequest<List<ContentComment>>(context) {
            @Override
            public List<ContentComment> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray array = json.optJSONArray("data");
                    if (array != null) {
                        ArrayList<ContentComment> list = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            ContentComment comment = gson.fromJson(array.optJSONObject(i).toString(),
                                    ContentComment.class);
                            list.add(comment);
                        }
                        return list;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .build(), page > 1)
                .setCallback(callback);
    }

    @Override
    public void addContentComment(long contentId, String text, long ref_commentId, DataRequest.DataCallback<Long> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl()
                + "/public/contents/" + contentId + "/comments";
        JSONObject json = new JSONObject();
        try {
            json.put("text", text);
            json.put("ref_comment_id", ref_commentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DataRequest<Long>(context) {
            @Override
            public Long jsonToBean(JSONObject json) {
                long commentId = 0;
                try {
                    if (json != null) {
                        String res = json.optString("res");
                        commentId = Long.valueOf(res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return commentId;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setRequestType(DataReuqestType.POST)
                        .setJsonParams(json)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }
}
