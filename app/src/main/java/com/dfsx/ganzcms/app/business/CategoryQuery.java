package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.model.Category;
import com.dfsx.ganzcms.app.model.CategoryPermission;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 有缓存的分类数据查询
 * Created by liuwb on 2016/11/8.
 */
public class CategoryQuery {

    private static CategoryQuery query;

    private Context context;

    private HashMap<String, CategoryPermission> categoryPermissionHashMap = new HashMap<>();

    private HashMap<String, Category> categoryHashMap = new HashMap<>();

    private ArrayList<Category> allCategory = new ArrayList<>();

    private CategoryManager categoryManager;

    private CategoryQuery(Context context) {
        this.context = context;
    }

    public static synchronized CategoryQuery getQuery(Context context) {
        if (query == null) {
            query = new CategoryQuery(context);
        }
        return query;
    }

    /**
     * 同步查询分类权限
     *
     * @param key
     * @return
     */
    public CategoryPermission queryPermissionSync(String key) {
        if (categoryPermissionHashMap.get(key)
                != null) {
            return categoryPermissionHashMap.get(key);
        }
        CategoryPermission cp = getCategoryManager().getSyncCategoryPermissionModel(key);
        categoryPermissionHashMap.put(key, cp);
        return cp;
    }

    public void queryPermissionAsync(final String key,
                                     final DataRequest.DataCallback<CategoryPermission> callback) {
        if (categoryPermissionHashMap.get(key)
                != null) {
            callback.onSuccess(false, categoryPermissionHashMap.get(key));
        } else {
            getCategoryManager().getCategoryPermission(key, new DataRequest.DataCallback<CategoryPermission>() {
                @Override
                public void onSuccess(boolean isAppend, CategoryPermission data) {
                    categoryPermissionHashMap.put(key, data);
                    if (callback != null) {
                        callback.onSuccess(isAppend, data);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    if (callback != null) {
                        callback.onFail(e);
                    }
                }
            });
        }
    }

    public Category queryCategorySync(String key) {
        if (categoryHashMap.get(key) != null) {
            return categoryHashMap.get(key);
        }
        Category c = getCategoryManager().getCategoryDetailsSync(key);
        categoryHashMap.put(key, c);
        return c;
    }

    public void queryCategoryAsync(final String key,
                                   final DataRequest.DataCallback<Category> callback) {
        if (categoryHashMap.get(key) != null) {
            callback.onSuccess(false, categoryHashMap.get(key));
        } else {
            getCategoryManager().getCategoryDetails(key, new DataRequest.DataCallback<Category>() {
                @Override
                public void onSuccess(boolean isAppend, Category data) {
                    categoryHashMap.put(key, data);
                    if (callback != null) {
                        callback.onSuccess(isAppend, data);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    if (callback != null) {
                        callback.onFail(e);
                    }
                }
            });
        }
    }

    private CategoryManager getCategoryManager() {
        if (categoryManager == null) {
            categoryManager = new CategoryManager(context);
        }
        return categoryManager;
    }

    public void queryAllCategory(final DataRequest.DataCallback<ArrayList<Category>> callback) {
        if (allCategory != null && !allCategory.isEmpty()) {
            callback.onSuccess(false, allCategory);
            return;
        }
        getCategoryManager().getAllCategory(new DataRequest.DataCallback<ArrayList<Category>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<Category> data) {
                allCategory = data;
                callback.onSuccess(isAppend, data);
            }

            @Override
            public void onFail(ApiException e) {
                allCategory.clear();
                callback.onFail(e);
            }
        });
    }

    public ArrayList<Category> queryAllCategorySync() {
        if (allCategory != null && !allCategory.isEmpty()) {
            return allCategory;
        }
        allCategory = getCategoryManager().getAllCategorySyncList();
        return allCategory;
    }
}
