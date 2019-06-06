package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.util.Log;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.ganzcms.app.model.DisclosureItem;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/10/10
 */
public class CommunityListManager extends DataFileCacheManager<ArrayList<DisclosureItem>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String COMMUNITYLISTMANAGER = "CommunityListManager.tx";
    private AppSession mSession;
    private long mColumnType;


    public CommunityListManager(Context context, String accountId, long columnType) {
        super(context, accountId, COMMUNITYLISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public CommunityListManager(Context context, String accountId, int columnType, boolean isLooper) {
        super(context, accountId, COMMUNITYLISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public void setColumnType(int columnType) {
        this.mColumnType = columnType;
    }

    public void start(boolean bAddTail, int offset) {
        String param = "/services/services_baoliao_list.json?";
        getDocObjectParam(bAddTail, param, offset);
    }

    private void getDocObjectParam(boolean bAddTail, String param, long offset) {
        String url = mSession.getBaseUrl();
        url += param;
        url += "page=" + offset + "";
        Log.e("TAG", "http url == " + url);
        getData(new HttpParamsBuilder().
                setUrl(url).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail);
    }

    @Override
    public ArrayList<DisclosureItem> jsonToBean(JSONObject jsonObject) {
        ArrayList<DisclosureItem> dlist = null;
        dlist = assembleNodes(jsonObject);
        return dlist;
    }

    ArrayList<DisclosureItem> assembleNodes(JSONObject jsonObject) {
        ArrayList<DisclosureItem> dlist = null;
        try {
            JSONArray result = jsonObject.optJSONArray("result");
            if (result != null) {
                dlist = new ArrayList<DisclosureItem>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = (JSONObject) result.get(i);
                    DisclosureItem snews = new DisclosureItem();
                    snews.id = item.getLong("nid");
                    snews.title = item.getString("node_title");
                    snews.username = item.optString("users_node_name");
                    snews.images = "";
                    if (!item.isNull("users_node_picture_uri")) {
                        String imgPath = item.getString("users_node_picture_uri");
                        int lastInde = imgPath.lastIndexOf("//");
                        imgPath = imgPath.substring(lastInde + 2, imgPath.length());
                        snews.userImag = imgPath;
                    }
                    long ctime = item.getLong("node_created");
                    snews.time = ctime;
                    snews.uId = item.getLong("node_uid");
                    snews.content = item.getString("body");
                    snews.commendNum = item.getInt("node_comment_statistics_comment_count");
                    JSONArray re = item.optJSONArray("field_baoliao_image");
                    String imageUrl = "";
                    if (re != null && re.length() > 0) {
                        imageUrl = UtilHelp.getImagePath(re.getString(0));
                        snews.images = imageUrl;
                        if (!imageUrl.equals("")) {
                            snews.imgWidth = UtilHelp.getImageWidth(re.getString(0));
                            snews.imgHeight = UtilHelp.getImageHeight(re.getString(0));
                            snews.imgs = new ArrayList<String>();
                            for (int j = 0; j < re.length(); j++) {
                                snews.imgs.add(UtilHelp.getImagePath(re.getString(j)));
                            }
                        }
                    }
                    boolean bShow = true;
                    if (("").equals(snews.images)) {
                        bShow = false;
                    }
                    snews.type = bShow == true ? 1 : 0;
                    dlist.add(snews);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }
}
