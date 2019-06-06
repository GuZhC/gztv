package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.Category;
import com.dfsx.ganzcms.app.model.CategoryPermission;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/10/24.
 */
public class CategoryManager {

    public static final String ALL_CATEGORY_URL = "/public/categories";

    private Context context;

    public CategoryManager(Context context) {
        this.context = context;
    }

    public void getAllCategory(DataRequest.DataCallback<ArrayList<Category>> callback) {
        String url = getLiveUrl() + ALL_CATEGORY_URL;
        new DataRequest<ArrayList<Category>>(context) {

            @Override
            public ArrayList<Category> jsonToBean(JSONObject json) {
                ArrayList<Category> categories = new ArrayList<Category>();
                Gson gson = new Gson();
                JSONArray array = json.optJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    Category category = gson.fromJson(jsonObject.toString(), Category.class);
                    categories.add(category);
                }
                return categories;
            }
        }.getData(new DataRequest.HttpParamsBuilder().
                setUrl(url).build(), false).
                setCallback(callback);
    }

    public ArrayList<Category> getAllCategorySyncList() {
        String url = getLiveUrl() + ALL_CATEGORY_URL;
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        JSONObject json = null;
        try {
            json = JsonCreater.jsonParseString(res);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        ArrayList<Category> categories = new ArrayList<Category>();
        Gson gson = new Gson();
        JSONArray array = json.optJSONArray("result");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            Category category = gson.fromJson(jsonObject.toString(), Category.class);
            categories.add(category);
        }
        return categories;
    }

    public void getCategoryDetails(String key, DataRequest.DataCallback<Category> callback) {
        String url = getLiveUrl() + "/public/categories/" + key;
        new DataRequest<Category>(context) {

            @Override
            public Category jsonToBean(JSONObject json) {
                Gson gson = new Gson();
                Category category = gson.fromJson(json.toString(), Category.class);
                return category;
            }
        }.getData(new DataRequest.HttpParamsBuilder().
                setUrl(url).build(), false).
                setCallback(callback);
    }

    public Category getCategoryDetailsSync(String key) {
        String url = getLiveUrl() + "/public/categories/" + key;
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        try {
            StringUtil.checkHttpResponseError(res);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        Category category = gson.fromJson(res, Category.class);
        return category;
    }

    private String getLiveUrl() {
        return App.getInstance().getmSession().getLiveServerUrl();
    }

    public void getCategoryPermission(String key,
                                      DataRequest.DataCallback<CategoryPermission> callback) {
        String url = getLiveUrl() + "/public/categories/" +
                key + "/permissions";
        new DataRequest<CategoryPermission>(context) {

            @Override
            public CategoryPermission jsonToBean(JSONObject json) {
                Gson gson = new Gson();
                CategoryPermission categoryPermission = gson.fromJson(json.toString(), CategoryPermission.class);
                return categoryPermission;
            }
        }.getData(new DataRequest.HttpParamsBuilder().
                setUrl(url).build(), false).
                setCallback(callback);
    }

    public String getSyncCategoryPermission(String key) {
        String url = getLiveUrl() + "/public/categories/" +
                key + "/permissions";
        return HttpUtil.executeGet(url, new HttpParameters(), null);
    }

    public CategoryPermission getSyncCategoryPermissionModel(String key) {
        String url = getLiveUrl() + "/public/categories/" +
                key + "/permissions";
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        JSONObject json = null;
        try {
            json = JsonCreater.jsonParseString(res);
            StringUtil.checkHttpResponseError(json.toString());
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        CategoryPermission cp = gson.fromJson(json.toString(),
                CategoryPermission.class);

        return cp;
    }

    public void getAllCategoryPermissions(final Observer<List<Category>> callBack) {
        String cUrl = getLiveUrl() + "/public/categories";
        getAllCategory(new DataRequest.DataCallback<ArrayList<Category>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<Category> categories) {
                Observable.from(categories)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<Category, Category>() {
                            @Override
                            public Category call(Category category) {
                                try {
                                    String respone = getSyncCategoryPermission(category.getKey());
                                    JSONObject json = JsonCreater.jsonParseString(respone);
                                    StringUtil.checkHttpResponseError(json.toString());
                                    Gson gson = new Gson();
                                    CategoryPermission cp = gson.fromJson(json.toString(),
                                            CategoryPermission.class);
                                    category.setPermission(cp);
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }
                                return category;
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(callBack);
            }

            @Override
            public void onFail(ApiException e) {
                callBack.onError(e);
            }
        });
    }
}
