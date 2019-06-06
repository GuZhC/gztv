package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.lzcms.liveroom.LiveRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.GuessRoomInfo;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveGuessRulesFragment extends Fragment {
    private Activity act;
    private TextView tv;
    private boolean isInitView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        tv = new TextView(getContext());
        tv.setTextColor(getContext().getResources().getColor(R.color.black_36));
        tv.setTextSize(17);
        tv.setPadding(PixelUtil.dp2px(act, 10), 0, PixelUtil.dp2px(act, 10), PixelUtil.dp2px(act, 10));
        isInitView = true;
        if (getGuessInfo() != null) {
            updateGuessInfo(getGuessInfo());
        }
        return tv;
    }

    public void updateGuessInfo(GuessRoomInfo guessRoomInfo) {
        if (guessRoomInfo != null && isInitView) {
            tv.setText(guessRoomInfo.getIntroduction());
        }

    }

    private GuessRoomInfo getGuessInfo() {
        if (act instanceof LiveRoomActivity) {
            return ((LiveRoomActivity) act).getGuessRoomInfo();
        }

        return null;
    }
}
