package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.dfsx.lzcms.liveroom.R;

/**
 * ListView 的空数据View，
 * 可以设置两中布局，
 * 1、加载中的布局， 调用loading显示
 * 2、加载完毕空数据的布局， 调用loadIOver显示
 * Created by liuwb on 2016/10/31.
 */
public class EmptyView extends FrameLayout {
    private Context context;

    private FrameLayout loadingContainer;

    private FrameLayout loadOverContainer;

    private int maxWith;

    private int maxHeight;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public static EmptyView newInstance(Context context) {
        EmptyView emptyView = new EmptyView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        emptyView.setLayoutParams(params);
        return emptyView;
    }

    private void init() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        maxWith = wm.getDefaultDisplay().getWidth();
        maxHeight = wm.getDefaultDisplay().getHeight();
        LayoutInflater.from(context).
                inflate(R.layout.empty_container_layout, this);
        loadingContainer = (FrameLayout) findViewById(R.id.loading_layout);

        loadOverContainer = (FrameLayout) findViewById(R.id.loaded_layout);

        setLoadingView(R.layout.loading_layout);

        loading();
    }

    public void setView(View loadingView, View loadOverView) {
        setLoadingView(loadingView);
        setLoadOverView(loadOverView);
    }

    public void setView(int loadingId, int loadOverId) {
        setLoadingView(loadingId);
        setLoadOverView(loadOverId);
    }

    public void setLoadingView(int id) {
        View v = LayoutInflater.from(context).inflate(id, null);
        setLoadingView(v);
    }

    public void setLoadingView(View v) {
        loadingContainer.removeAllViews();
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        loadingContainer.addView(v, maxWith, maxHeight);
    }

    public void setLoadOverView(int id) {
        View v = LayoutInflater.from(context).inflate(id, null);
        setLoadOverView(v);
    }

    public void setLoadOverView(View v) {
        loadOverContainer.removeAllViews();
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        loadOverContainer.addView(v, maxWith, maxHeight);
    }

    public void loading() {
        loadingContainer.setVisibility(VISIBLE);
        loadOverContainer.setVisibility(GONE);
    }

    public void loadOver() {
        loadingContainer.setVisibility(GONE);
        loadOverContainer.setVisibility(VISIBLE);
    }
}
