package com.dfsx.ganzcms.app.business;

import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.Category;
import com.dfsx.lzcms.liveroom.business.ICallBack;

import java.util.ArrayList;

/**
 * 所用的channel的分类
 * Created by liuwb on 2016/10/27.
 */
public class AllCategoryManager {

    private static AllCategoryManager instance = new AllCategoryManager();

    private ArrayList<Category> categoryList;

    private CategoryManager cManager = new CategoryManager(App.getInstance().
            getApplicationContext());

    private AllCategoryManager() {

    }

    public static AllCategoryManager getInstance() {
        return instance;
    }

    public void get(final ICallBack<ArrayList<Category>> callBack) {
        if (categoryList != null) {
            callBack.callBack(categoryList);
            return;
        }
        cManager.getAllCategory(new DataRequest.DataCallback<ArrayList<Category>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<Category> data) {
                categoryList = data;
                callBack.callBack(data);
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                callBack.callBack(null);
            }
        });
    }
}
