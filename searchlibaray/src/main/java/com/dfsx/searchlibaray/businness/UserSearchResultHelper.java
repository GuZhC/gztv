package com.dfsx.searchlibaray.businness;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.searchlibaray.AppSearchManager;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchResult;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserSearchResultHelper extends BaseSearchResultHelper {

    public UserSearchResultHelper(SearchResult searchResult, boolean isAppend, Object searchTag, DataRequest.DataCallback<List<ISearchData>> callback) {
        super(searchResult, isAppend, searchTag, callback);
    }

    @Override
    public ArrayList<ISearchData> getRealDataByIds(Long... ids) {
        String userListurl = AppSearchManager.getInstance().getSearchConfig().getHttpBaseUrl() +
                "/public/users/multiple/";
        String userFollowedUrl = AppSearchManager.getInstance().getSearchConfig().getHttpBaseUrl() +
                "/public/users/current/followed/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                userListurl += appendString;
                userFollowedUrl += appendString;
            }
            String res = HttpUtil.executeGet(userListurl, new HttpParameters(), null);
            try {
                StringUtil.checkHttpResponseError(res);
                JSONArray array = new JSONArray(res);
                if (array != null) {
                    Gson gson = new Gson();
                    ArrayList<ISearchData> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        UserInfo data = gson.fromJson(array.optJSONObject(i).toString(),
                                UserInfo.class);
                        list.add(data);
                    }
                    //设置是否关注
                    if (AppSearchManager.getInstance().getSearchConfig().isLogin()) {
                        String followRes = HttpUtil.executeGet(userFollowedUrl,
                                new HttpParameters(), AppSearchManager.getInstance()
                                        .getSearchConfig().getCurrentToken());
                        try {
                            StringUtil.checkHttpResponseError(followRes);
                            JSONObject followJson = new JSONObject(followRes);
                            for (ISearchData item : list) {
                                UserInfo user = (UserInfo) item;
                                if (user != null) {
                                    JSONObject itemJson = followJson.optJSONObject(user.getId() + "");
                                    if (itemJson != null) {
                                        user.setFanned(itemJson.optBoolean("fanned"));
                                        user.setFollowed(itemJson.optBoolean("followed"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
