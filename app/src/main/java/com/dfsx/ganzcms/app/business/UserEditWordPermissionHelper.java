package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.util.Log;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.UserCMSPlatformInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * 快速投稿权限的验证
 */
public class UserEditWordPermissionHelper {

    private Context context;

    private String baseCmsUrl;

    public UserEditWordPermissionHelper(Context context) {
        this.context = context;
        this.baseCmsUrl = App.getInstance().getmSession().getContentcmsServerUrl();
    }

    public void isEditWordPermission(final EditPermissionCallBack callBack) {
        Observable.just(null)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Object o) {
                        String url = baseCmsUrl +
                                "/public/users/current";
                        String cmsPlatformInfoRes = HttpUtil.executeGet(url, new HttpParameters(),
                                App.getInstance().getCurrentToken());
                        UserCMSPlatformInfo info = new Gson().fromJson(cmsPlatformInfoRes,
                                UserCMSPlatformInfo.class);
                        if (info == null || info.getId() == 0 || !info.isHasAddWordPermission()) {
                            return Observable.just(false);
                        }
                        return Observable.just(true);
                    }
                })
                .map(new Func1<Boolean, TempData>() {
                    @Override
                    public TempData call(Boolean aBoolean) {
                        if (!aBoolean) {
                            return new TempData(false, null);
                        }
                        String allUrl = baseCmsUrl + "/public/users/current/contribute/columns";
                        String allWordColumnsRes = HttpUtil.executeGet(allUrl, new HttpParameters(),
                                App.getInstance().getCurrentToken());
                        Log.e("TAG", "allWordColumnsRes == " + allWordColumnsRes);
                        JSONArray allArray = null;
                        try {
                            allArray = new JSONArray(allWordColumnsRes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (allArray != null && allArray.length() > 0) {
                            ArrayList<Long> permissionColumnIds = new ArrayList<>();
                            for (int i = 0; i < allArray.length(); i++) {
                                permissionColumnIds.add(allArray.
                                        optJSONObject(i).optLong("id"));
                            }
                            return new TempData(true, permissionColumnIds);
                        }
                        return new TempData(false, null);
                    }
                })
                .subscribe(new Observer<TempData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TempData tempData) {
                        if (callBack != null) {
                            callBack.callBack(tempData.isPermission, tempData.permissionColumnIds);
                        }
                    }
                });
    }


    public interface EditPermissionCallBack {
        void callBack(boolean isPermission, ArrayList<Long> permissionColumnIds);
    }

    class TempData {
        public boolean isPermission;
        public ArrayList<Long> permissionColumnIds;

        public TempData(boolean is, ArrayList<Long> ids) {
            this.isPermission = is;
            this.permissionColumnIds = ids;
        }
    }
}
