package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.view.View;
import com.dfsx.ganzcms.app.adapter.LiveRoomAdapter;
import com.dfsx.ganzcms.app.adapter.LiveUserGridRoomAdapter;
import com.dfsx.ganzcms.app.adapter.LiveUserListRoomAdapter;
import com.dfsx.ganzcms.app.model.Room;

import java.util.List;

/**
 * 新版本的直播页面
 * Created by liuwb on 2017/6/14.
 */
public class LiveUserRoomFragment extends LiveFragment implements ILiveShowStyleChange {

    private LiveUserListRoomAdapter listAdapter;
    private LiveUserGridRoomAdapter gridAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topBarView.setVisibility(View.GONE);
    }

    @Override
    protected void setListAdapter() {
        listAdapter = new LiveUserListRoomAdapter(getContext(), null);
        gridAdapter = new LiveUserGridRoomAdapter(getContext(), null);
        //默认显示的列表
        roomAdapter = listAdapter;
        setListViewAdapter();

        gridAdapter.setOnGridItemClickListener(new LiveUserGridRoomAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemClick(Room room) {
                goRoom(room);
            }
        });
    }

    @Override
    protected void onListViewItemClick(int position) {
        if (roomAdapter == gridAdapter) {
        } else {
            super.onListViewItemClick(position);
        }
    }

    private void setListViewAdapter() {
        pullToRefreshListView.setAdapter(roomAdapter);
    }

    @Override
    public void onStyleChange(int style) {
        LiveRoomAdapter changeToAdapter = null;
        if (style == ILiveShowStyleChange.STYLE_GRID) {
            changeToAdapter = gridAdapter;
        } else if (style == ILiveShowStyleChange.STYLE_LIST) {
            changeToAdapter = listAdapter;
        }
        if (roomAdapter != changeToAdapter && changeToAdapter != null) {
            List<Room> showList = roomAdapter.getData();
            roomAdapter = changeToAdapter;
            setListViewAdapter();
            roomAdapter.update(showList, false);
        }
    }
}
