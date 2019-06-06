package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.Level;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserLevelManager {

    private static UserLevelManager instance = new UserLevelManager();

    private List<Level> allLevelList;

    private UserLevelManager() {

    }

    public static UserLevelManager getInstance() {
        return instance;
    }

    public void findUserLevel(Context context, ICallBack<List<Level>> callBack, long... ids) {
        if (allLevelList == null || allLevelList.isEmpty()) {
            getNetLevelList(context, new AsyncAllListCallBack(callBack, ids));
        } else {
            callBack.callBack(findLevelFromList(allLevelList));
        }
    }

    public List<Level> findUserLevelSync(Context context, long... ids) {
        if (allLevelList == null || allLevelList.isEmpty()) {
            List<Level> allList = getAllLevelListSync(context);
            return findLevelFromList(allList, ids);
        } else {
            return findLevelFromList(allLevelList, ids);
        }
    }

    public Level findLevelSync(Context context, long id) {
        if (allLevelList == null || allLevelList.isEmpty()) {
            List<Level> allList = getAllLevelListSync(context);
            return findLevelFromList(allList, id);
        } else {
            return findLevelFromList(allLevelList, id);
        }
    }

    public void findLevel(Context context, long id, final ICallBack<Level> callBack) {
        if (allLevelList == null || allLevelList.isEmpty()) {
            getNetLevelList(context, new AsyncAllListCallBack(new ICallBack<List<Level>>() {
                @Override
                public void callBack(List<Level> data) {
                    if (data != null && data.size() > 0) {
                        callBack.callBack(data.get(0));
                    } else {
                        callBack.callBack(null);
                    }
                }
            }, id));
        } else {
            callBack.callBack(findLevelFromList(allLevelList, id));
        }
    }

    private List<Level> findLevelFromList(List<Level> list, long... ids) {
        List<Level> levelList = new ArrayList<>();
        for (long id : ids) {
            Level level = findLevelFromList(list, id);
            levelList.add(level);
        }
        return levelList;
    }

    private Level findLevelFromList(List<Level> list, long id) {
        if (list != null) {
            for (Level l : list) {
                if (id == l.getId()) {
                    return l;
                }
            }
        }
        return null;
    }

    public void showLevelImage(Context context, long levelId, ImageView imageView) {
        findLevel(context, levelId, new ImageViewCallBack(context, imageView));
    }

    private List<Level> getAllLevelListSync(Context context) {
        String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                "/public/user-levels";
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        try {
            LogUtils.d("http", "user-levels == " + res);
            StringUtil.checkHttpResponseError(res);
            JSONArray array = new JSONArray(res);
            if (array != null) {
                List<Level> list = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    Level level = gson.fromJson(item.toString(), Level.class);
                    list.add(level);
                }
                allLevelList = list;
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        allLevelList = null;
        return null;

    }

    private void getNetLevelList(Context context, final ICallBack<List<Level>> callBack) {
        String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                "/public/user-levels";
        new DataRequest<List<Level>>(context) {

            @Override
            public List<Level> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray array = json.optJSONArray("result");
                    if (array != null) {
                        List<Level> list = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.optJSONObject(i);
                            Level level = gson.fromJson(item.toString(), Level.class);
                            list.add(level);
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
                        .build(), false)
                .setCallback(new DataRequest.DataCallback<List<Level>>() {
                    @Override
                    public void onSuccess(boolean isAppend, List<Level> data) {
                        allLevelList = data;
                        if (callBack != null) {
                            callBack.callBack(data);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        allLevelList = null;
                        if (callBack != null) {
                            callBack.callBack(null);
                        }
                    }
                });
    }

    class AsyncAllListCallBack implements ICallBack<List<Level>> {

        private ICallBack<List<Level>> callBack;
        private long[] ids;

        public AsyncAllListCallBack(ICallBack<List<Level>> callBack, long[] ids) {
            this.callBack = callBack;
            this.ids = ids;
        }

        public AsyncAllListCallBack(ICallBack<List<Level>> callBack, long id) {
            this.callBack = callBack;
            this.ids = new long[1];
            ids[0] = id;
        }

        @Override
        public void callBack(List<Level> data) {
            List<Level> levelList = null;
            if (data != null) {
                levelList = new ArrayList<>();
                if (ids != null) {
                    for (long id : ids) {
                        Level level = findLevelFromList(data, id);
                        levelList.add(level);
                    }
                }
            }
            callBack.callBack(levelList);
        }
    }

    class ImageViewCallBack implements ICallBack<Level> {

        private ImageView imageView;
        private Context context;

        public ImageViewCallBack(Context context, ImageView imageView) {
            this.imageView = imageView;
            this.context = context;
        }

        @Override
        public void callBack(Level data) {
            String imageUrl = data != null ? data.getIconUrl() : "";
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.color.transparent)
                    .crossFade()
                    .into(imageView);
        }
    }
}
