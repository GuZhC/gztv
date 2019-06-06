package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/9/13.
 */
public class RedHeaderFragment extends Fragment {

    protected Activity act;
    protected Context context;
    protected FrameLayout contentContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        View v = inflater.inflate(R.layout.frag_red_header, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        contentContainer = (FrameLayout) v.findViewById(R.id.bottom_header_container);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    protected void setContentView(int layoutId) {
        View v = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(v);
    }

    protected void setContentView(View v) {
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        contentContainer.addView(v);
    }

    public int getContainerId() {
        return R.id.bottom_header_container;
    }
}
