package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.HostInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class HostPersonListHelper implements IGetHostPersonList {

    private Context context;
    private long hostColumnId;

    public HostPersonListHelper(Context context) {
        this.context = context;
    }


    @Override
    public void getHostList(long hostColumnId, final int page, final int pageSize, final DataRequest.DataCallback<ArrayList<HostInfo>> callback) {
        if (hostColumnId != 0) {
            getHostListByNet(hostColumnId, page, pageSize, callback);
        } else {
            if (this.hostColumnId != hostColumnId && this.hostColumnId != 0) {
                getHostListByNet(this.hostColumnId, page, pageSize, callback);
            } else {
                Observable.just(null)
                        .observeOn(Schedulers.io())
                        .map(new Func1<Object, Long>() {
                            @Override
                            public Long call(Object o) {
                                long id = ColumnBasicListManager.getInstance().findIdByName("host");
                                return id;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                getHostListByNet(aLong, page, pageSize, callback);
                            }
                        });
            }
        }
    }


    private void getHostListByNet(long hostColumnId, int page, int pageSize, DataRequest.DataCallback<ArrayList<HostInfo>> callback) {
        this.hostColumnId = hostColumnId;
        String url = App.getInstance().getmSession().getContentcmsServerUrl()
                + "/public/columns/" + hostColumnId + "/contents?page=" +
                page + "&size=" + pageSize;

        DataFileCacheManager<ArrayList<HostInfo>> hostFileRequest = new DataFileCacheManager<ArrayList<HostInfo>>(context, "all",
                "dazhou_host_person_list.txt") {
            @Override
            public ArrayList<HostInfo> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray arr = json.optJSONArray("data");
                    if (arr != null && arr.length() > 0) {
                        ArrayList<HostInfo> infos = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            HostInfo info = gson.fromJson(arr.optJSONObject(i).toString(), HostInfo.class);
                            infos.add(info);
                        }
                        return infos;
                    }
                }
                return null;
            }
        };

        hostFileRequest.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .setRequestType(DataReuqestType.GET)
                .build(), page > 1)
                .setCallback(callback);
    }
}
