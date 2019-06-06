package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/6/1
 */
public class HeadlineListManager extends DataFileCacheManager<ArrayList<ContentCmsEntry>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String HEADLINELISTMANAGER = App.getInstance().getPackageName() + "HeadlineListManager.tx";
    private AppSession mSession;
    private long mColumnType;
    private String _columnCode;
    private boolean isShowUpdate = false;
    private ContentCmsApi contentCmsApi;

    public HeadlineListManager(Context context, String accountId, long columnType, String columnCode) {
        super(context, accountId, HEADLINELISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
        this._columnCode = columnCode;
        contentCmsApi = new ContentCmsApi(context);
    }

    public HeadlineListManager(Context context, String accountId, long columnType, boolean isLooper) {
        super(context, accountId, HEADLINELISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
        contentCmsApi = new ContentCmsApi(context);
    }

    public boolean isShowUpdate() {
        return isShowUpdate;
    }

    public void setIsShowUpdate(boolean isShowUpdate) {
        this.isShowUpdate = isShowUpdate;
    }

    public void setColumnType(int columnType) {
        this.mColumnType = columnType;
    }

    public void start(boolean bAddTail, int offset) {
//        String param = "/services/";
//        switch ((int) mColumnType) {
//            case ColumnTYPE.POLITICS_NEWS_TYPE:
//                param += "sevice_minsheng_news.json?";
//                break;
//            case ColumnTYPE.INTERNAL_NEWS_TYPE:
//                param += "sevice_yaowen.json?";
//                break;
//            case ColumnTYPE.EXTERNAL_NEWS_TYPE:
//                param += "sevice_huati.json?";
//                break;
//        }
        getDocObjectParam(bAddTail, "", offset);
    }

    public void start(boolean bAddTail, boolean isReadlocal, int offset) {
        String url = mSession.getContentcmsServerUrl() + "/public/columns/";
//        if (this.mColumnType == 29) {
//            recommend
//            url += "/list/contents?";
//        } else {
//            url += mColumnType + "/contents?";
//        }
        if (TextUtils.equals(_columnCode, "home")) {
            url += "list/contents?";
        } else {
            url += mColumnType + "/contents?";
        }
        url += "page=" + offset + "";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail, isReadlocal);
    }

    public void find(String keyWord, int offset) {
        String url = mSession.getContentcmsServerUrl() + "/public/contents?keyword=" + keyWord;
//        url += "page=" + offset + "";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), false, false);
    }

    private void getDocObjectParam(boolean bAddTail, String param, long offset) {
        String url = mSession.getContentcmsServerUrl() + "/public/columns/";
        if (this.mColumnType == 29) {
            //recommend
            url += "list/contents?";
        } else {
            url += mColumnType + "/contents?";
        }
        url += "page=" + offset + "";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail);
    }

    @Override
    public ArrayList<ContentCmsEntry> jsonToBean(JSONObject jsonObject) {
        ArrayList<ContentCmsEntry> socityNewsAarry = null;
        try {
            JSONArray result = jsonObject.optJSONArray("data");
            if (result != null) {
                socityNewsAarry = new ArrayList<ContentCmsEntry>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = (JSONObject) result.get(i);
                    ContentCmsEntry entry = new Gson().fromJson(item.toString(), ContentCmsEntry.class);
//                    if (TextUtils.equals(entry.getType(), "quick-entry"))
//                        continue;
                    //判断是不是多图
                    int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                    entry.setShowType(contentCmsApi.getShowModeType(entry.getList_item_mode(), entry.getType()));
                    int modeType = contentCmsApi.getModeType(entry.getType(), thumn_size);
                    if (modeType == 3) {
                        // show 直播 特殊处理
                        entry.setShowType(modeType);
                        JSONObject extendsObj = item.optJSONObject("extension");
                        if (extendsObj != null) {
                            JSONObject showObj = extendsObj.optJSONObject("show");
                            if (showObj != null) {
                                ContentCmsEntry.ShowExtends showExtends = new Gson().fromJson(showObj.toString(), ContentCmsEntry.ShowExtends.class);
                                entry.setShowExtends(showExtends);
                            }
                        }
                    }
                    //特殊處理廣告
                    if (entry.getShowType() == 6 && TextUtils.equals("ad", entry.getType().toLowerCase().trim())) {
                        JSONObject obj = item.optJSONObject("fields");
                        if (obj != null) {
                            String url = obj.optString("ad_link_url");
                            String introd = obj.optString("ad_intro");
                            entry.setUrl(url);
                            entry.setSummary(introd);
                        }
                    }
                    //判断应急消息类型的icon_id，并获取图标
                    if (item.optJSONObject("fields")!= null){
                        if (!TextUtils.isEmpty(item.optJSONObject("fields").optString("emergency_type"))){
                            String emergencyType = item.optJSONObject("fields").optString("emergency_type");
                            entry.setEmergencyType(emergencyType);
                        }else if (item.optJSONObject("fields").optLong("emergency_icon") != 0L){
                            long icon_id = item.optJSONObject("fields").optLong("emergency_icon");
                            entry.setEmergencyIcon(icon_id);
                            entry.setEmergencyIcons(contentCmsApi.getPictures(new Long[]{icon_id}));
                        }

                    }

                    //处理横幅类型
                    if (modeType == 12 && item.optJSONObject("fields")!= null){
                        if (item.optJSONObject("fields").optLong("banner_internal_link") != 0L){
                            long icon_id = item.optJSONObject("fields").optLong("banner_internal_link");
                            entry.setId(icon_id);
                        }else if (item.optJSONObject("fields").optLong("banner_link_column") != 0L){
                            long icon_id = item.optJSONObject("fields").optLong("banner_link_column");
                            entry.setId(icon_id);
                        }
                    }

                    entry.setModeType(modeType);
                    socityNewsAarry.add(entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socityNewsAarry;
    }
}
