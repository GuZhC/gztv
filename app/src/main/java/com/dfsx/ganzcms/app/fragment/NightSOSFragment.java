package com.dfsx.ganzcms.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.dfsx.ganzcms.app.business.SOSMessageHelper;
import rx.functions.Action1;

public class NightSOSFragment extends Fragment {

    private View bkgView;
    private SOSMessageHelper sosMessageHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bkgView = new RelativeLayout(getContext());
        bkgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        bkgView.setBackgroundColor(Color.WHITE);
        return bkgView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sosMessageHelper = new SOSMessageHelper(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (bkgView != null) {
                    int bkgColor = aBoolean ? Color.WHITE : Color.BLACK;
                    bkgView.setBackgroundColor(bkgColor);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sosMessageHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        sosMessageHelper.stop();
    }
}
