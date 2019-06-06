package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.NewsGridItem;
import com.dfsx.ganzcms.app.model.NewsGridMenu;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class CityGridDataHelper {

    private Context context;
    private ContentCmsApi contentCmsApi;

    public CityGridDataHelper(Context context) {
        this.context = context;
        contentCmsApi = new ContentCmsApi(context);
    }

    public void getColumnContentList(long columnId, int page, int size, DataRequest.DataCallback<List<NewsGridItem>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                "/public/columns/" + columnId +
                "/contents?page=" + page + "&size=" + size;
        new DataRequest<List<NewsGridItem>>(context) {
            @Override
            public List<NewsGridItem> jsonToBean(JSONObject json) {
                ArrayList<NewsGridItem> list = new ArrayList<>();
                try {
                    JSONArray array = json.optJSONArray("data");
                    Gson g = new Gson();
                    for (int n = 0; n < array.length(); n++) {
                        JSONObject item = array.optJSONObject(n);
                        ContentCmsEntry entry = g.fromJson(item.toString(), ContentCmsEntry.class);
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
                        entry.setModeType(modeType);
                        list.add(new NewsGridItem(entry));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return list;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setRequestType(DataReuqestType.GET)
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), page > 1)
                .setCallback(callback);
    }

    public void getColumn2DataList(ColumnCmsEntry cmsEntry, Action1<List<NewsGridItem>> action) {
        Observable.just(cmsEntry)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<ColumnCmsEntry, Observable<ColumnCmsEntry>>() {
                    @Override
                    public Observable<ColumnCmsEntry> call(ColumnCmsEntry cmsEntry) {
                        if (cmsEntry != null && cmsEntry.getDlist() != null) {
                            return Observable.from(cmsEntry.getDlist());
                        }
                        return Observable.just(null);
                    }
                })
                .concatMap(new Func1<ColumnCmsEntry, Observable<NewsGridItem>>() {
                    @Override
                    public Observable<NewsGridItem> call(ColumnCmsEntry cmsEntry) {
                        if (cmsEntry != null) {
                            String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                                    "/public/columns/" + cmsEntry.getId() +
                                    "/contents?page=1&size=2";
                            String res = HttpUtil.executeGet(url, new HttpParameters(), null);
                            ArrayList<NewsGridItem> list = new ArrayList<>();
                            try {
                                JSONObject resJson = new JSONObject(res);
                                JSONArray array = resJson.optJSONArray("data");
                                Gson g = new Gson();
                                for (int n = 0; n < array.length() && n < 2; n++) {
                                    JSONObject item = array.optJSONObject(n);
                                    ContentCmsEntry entry = g.fromJson(item.toString(), ContentCmsEntry.class);
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
                                    entry.setModeType(modeType);
                                    list.add(new NewsGridItem(entry));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            for (int i = list.size(); i < 2; i++) {
                                list.add(new NewsGridItem(null));
                            }
                            return Observable.from(list);
                        }
                        return Observable.just(null);
                    }
                })
                .toList()
                .map(new Func1<List<NewsGridItem>, ArrayList<NewsGridItem>>() {
                    @Override
                    public ArrayList<NewsGridItem> call(List<NewsGridItem> newsGridItems) {
                        ArrayList<NewsGridItem> list = new ArrayList<>();
                        if (newsGridItems != null) {
                            for (NewsGridItem item : newsGridItems) {
                                if (item != null) {
                                    list.add(item);
                                }
                            }
                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }


    public List<NewsGridMenu> getNewsGridMenu(ColumnCmsEntry cmsEntry) {
        if (cmsEntry != null && cmsEntry.getDlist() != null && cmsEntry.getDlist().size() > 0) {
            List<NewsGridMenu> list = new ArrayList<>();
            for (ColumnCmsEntry entry : cmsEntry.getDlist()) {
                list.add(new NewsGridMenu(entry));
            }

            return list;
        }
        return null;
    }
}
