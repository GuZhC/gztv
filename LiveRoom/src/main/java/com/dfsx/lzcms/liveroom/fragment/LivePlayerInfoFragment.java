package com.dfsx.lzcms.liveroom.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.LiveRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.model.GuessPlayer;
import com.dfsx.lzcms.liveroom.model.GuessRoomInfo;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LivePlayerInfoFragment extends AbsListFragment {

    private PlayerInfoAdapter adapter;
    private boolean isInit;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isInit = true;
        if (getGuessInfo() != null) {
            adapter.update(getGuessInfo().getPlayerList(), false);
        }
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new PlayerInfoAdapter(context);
        listView.setAdapter(adapter);
        listView.setBackgroundColor(context.getResources()
                .getColor(R.color.public_bgd));
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    public void updateGuessInfo(GuessRoomInfo guessRoomInfo) {
        if (guessRoomInfo != null && isInit) {
            adapter.update(guessRoomInfo.getPlayerList(), false);
        }
    }

    private GuessRoomInfo getGuessInfo() {
        if (act instanceof LiveRoomActivity) {
            return ((LiveRoomActivity) act).getGuessRoomInfo();
        }

        return null;
    }

    class PlayerInfoAdapter extends BaseListViewAdapter<GuessPlayer> {

        public PlayerInfoAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_player_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            CircleButton logo = holder.getView(R.id.person_logo);
            TextView name = holder.getView(R.id.person_name);
            TextView introduction = holder.getView(R.id.person_info_text);

            GuessPlayer player = list.get(position);
            name.setText(player.getName());
            introduction.setText(player.getIntroduction());
            LSLiveUtils.showUserLogoImage(context,
                    logo, player.getLogo());
        }
    }
}
