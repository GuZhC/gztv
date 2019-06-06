package com.dfsx.searchlibaray.businness;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.searchlibaray.AppSearchManager;
import com.dfsx.searchlibaray.R;
import com.dfsx.searchlibaray.view.SearchSuggestListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuggestListHelper {

    private ViewGroup container;
    private int left, right, top;
    private int height;

    private Context context;

    private SearchSuggestListView suggestListView;
    private SearchSuggestListView.OnSuggestItemClickListener itemClickListener;

    private FrameLayout suggestContainerView;

    private boolean isShowSuggestList;

    public SuggestListHelper(ViewGroup container, int left, int right, int top, int height) {
        this.container = container;
        this.left = left;
        this.right = right;
        this.top = top;
        this.height = height;
        context = container.getContext();
        suggestContainerView = new FrameLayout(context);
        suggestListView = new SearchSuggestListView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                height);
        suggestListView.setLayoutParams(params);

        suggestContainerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        suggestContainerView.setPadding(left, top, right, 0);
        suggestContainerView.setId(R.id.suggest_list_view);
        suggestContainerView.addView(suggestListView);
        suggestContainerView.setBackgroundResource(R.color.white);

        suggestListView.setBackgroundResource(R.drawable.shape_suggest_bkg);
        suggestListView.setOnSuggestItemClickListener(new SearchSuggestListView.OnSuggestItemClickListener() {
            @Override
            public void onSuggestClick(String suggestText) {
                if (itemClickListener != null) {
                    itemClickListener.onSuggestClick(suggestText);
                }
            }
        });
    }

    private void showListView() {
        isShowSuggestList = true;
        if (container != null) {
            boolean isHas = container.findViewById(R.id.suggest_list_view) != null;
            if (!isHas && suggestContainerView != null) {
                container.addView(suggestContainerView);
            }
        }
    }

    public void setOnSuggestItemClickListener(SearchSuggestListView.OnSuggestItemClickListener l) {
        this.itemClickListener = l;
    }

    public void remove() {
        if (isShowSuggestList) {
            if (suggestContainerView != null && container != null) {
                container.removeView(suggestContainerView);
            }
        }
        isShowSuggestList = false;
    }

    public boolean inRangeOfView(MotionEvent ev) {
        if (!isShowSuggestList) {
            return false;
        }
        int[] location = new int[2];
        suggestContainerView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x ||
                ev.getX() > (x + suggestContainerView.getWidth()) ||
                ev.getY() < y ||
                ev.getY() > (y + suggestContainerView.getHeight())) {
            return false;
        }
        return true;
    }

    public void getSuggest(String text, String source, String type,
                           int count) {
        if (TextUtils.isEmpty(text)) {
            remove();
            return;
        }
        getSuggestList(context, text, source, type, count, new DataRequest.DataCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(boolean isAppend, ArrayList<String> data) {
                if (data != null && data.size() > 0) {
                    showListView();
                    if (suggestListView != null) {
                        suggestListView.setList(data);
                    }
                } else {
                    remove();
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                remove();
            }
        });
    }

    public void getSuggest(String text, String source, String type) {
        getSuggest(text, source, type, 5);
    }

    public void getSuggest(String text) {
        getSuggest(text, "", "", 5);
    }

    public static void getSuggestList(Context context, String keyword, String source,
                                      String type, int count, DataRequest.DataCallback callback) {
        String url = AppSearchManager.getInstance().getSearchConfig()
                .getHttpBaseUrl() + "/public/site/suggests?keyword=" + keyword +
                "&source=" + source +
                "&type=" + type +
                "&count=" + count;

        new DataRequest<ArrayList<String>>(context) {
            @Override
            public ArrayList<String> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray array = json.optJSONArray("result");
                    if (array != null) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            list.add(array.optString(i));
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
                .setCallback(callback);
    }
}
