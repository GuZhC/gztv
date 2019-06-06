package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.UserLiveDataHelper;
import com.dfsx.ganzcms.app.model.ILiveService;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.LiveServiceRoomDetailsInfo;
import com.dfsx.ganzcms.app.model.Room;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.lzcms.liveroom.view.CollectionView;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.PullToRefreshExpandableRecyclerView;
import com.dfsx.lzcms.liveroom.view.async.AsyncExpandableListView;
import com.dfsx.lzcms.liveroom.view.async.AsyncExpandableListViewCallbacks;
import com.dfsx.lzcms.liveroom.view.async.AsyncHeaderViewHolder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动列表
 * Created by liuwb on 2017/7/13.
 */
public class MyLiveServiceListFragment extends AbsLoginRequestFragment implements AsyncExpandableListViewCallbacks<ILiveService, ILiveService.ILiveInputStream> {
    private static String[] PERMISSIONS_STREAM = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Activity activity;
    private Button btnStartLive;
    private PullToRefreshExpandableRecyclerView pullView;
    private AsyncExpandableListView<ILiveService, ILiveService.ILiveInputStream> expandableListView;
    private LinearLayout emptyContainer;
    private EmptyView emptyView;

    private UserLiveDataHelper helper;

    private int currentPage;

    private CollectionView.Inventory<ILiveService, ILiveService.ILiveInputStream> headerGroup;


    private ILiveService.ILiveInputStream selectedInputstream;
    private ILiveService selectedLiveService;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.frag_my_live_service_layout, null);
    }

    @Override
    public void onBeforeCheckLogin(View view, @Nullable Bundle savedInstanceState) {
        helper = new UserLiveDataHelper(getContext());
        pullView = (PullToRefreshExpandableRecyclerView) view.findViewById(R.id.pull_asyncExpandableCollectionView);
        expandableListView = (AsyncExpandableListView<ILiveService,
                ILiveService.ILiveInputStream>) pullView.getRefreshableView();
        emptyContainer = (LinearLayout) view.findViewById(R.id.empty_container);
        btnStartLive = (Button) view.findViewById(R.id.start_service_live);
        expandableListView.setCallbacks(this);
        pullView.setMode(PullToRefreshBase.Mode.BOTH);

        setEmptyView();
        initAction();
    }

    private void setEmptyView() {
        emptyView = EmptyView.newInstance(getContext());
        TextView tv = new TextView(getContext());
        tv.setText("没有可用活动直播列表");
        tv.setTextColor(getContext().getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        emptyContainer.addView(emptyView);
    }

    public void showEmptyView() {
        emptyView.loadOver();
        emptyContainer.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        emptyView.loadOver();
        emptyContainer.setVisibility(View.GONE);
    }

    protected void setEmptyViewVisible(List<Room> data) {
        if (data == null || data.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    @Override
    public void onLogined() {
        getMyServiceData(1);
    }

    private void initAction() {
        btnStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedInputstream != null && selectedLiveService != null) {
                    checkPermissionAndGoRecord();
                } else {
                    Toast.makeText(activity, "请选择输入机位", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pullView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getMyServiceData(1);
                selectedInputstream = null;
                selectedLiveService = null;
                btnStartLive.setEnabled(false);
                expandableListView.resetExpandedGroupOrdinal();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getMyServiceData(currentPage + 1);
            }
        });
    }

    private void checkPermissionAndGoRecord() {
        new TedPermission(activity)
                .setPermissions(PERMISSIONS_STREAM)
                .setDeniedMessage("没有可用的权限! 请先在设置中打开权限后再试")
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        LSUtils.goLiveServiceRecord(getActivity(), true, selectedLiveService.getServiceId(),
                                selectedInputstream.getInputRtmpUrl());
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(activity, "没有权限", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void updateGroupData(boolean isAdd, ArrayList<ILiveService> list) {
        if (headerGroup == null) {
            headerGroup = new CollectionView.Inventory<>();
        }
        if (!isAdd) {
            headerGroup.getGroups().clear();
        }
        if (list != null && !list.isEmpty()) {
            for (ILiveService service : list) {
                CollectionView.InventoryGroup<ILiveService, ILiveService.ILiveInputStream> group =
                        headerGroup.newGroup(headerGroup.getGroupCount());
                group.setHeaderItem(service);
            }
        }
        expandableListView.updateInventory(headerGroup);
    }

    private void getMyServiceData(int page) {
        this.currentPage = page;
        helper.getUserLiveRoomDataList(0, page, 20, 2, 0,
                new DataRequest.DataCallback<List<Room>>() {
                    @Override
                    public void onSuccess(boolean isAppend, List<Room> data) {
                        if (!isAppend) {
                            setEmptyViewVisible(data);
                        }
                        if (data != null && !data.isEmpty()) {
                            ArrayList<ILiveService> liveServiceList = new ArrayList<ILiveService>();
                            for (Room r : data) {
                                if (r instanceof LiveInfo) {
                                    LiveInfo info = (LiveInfo) r;
                                    liveServiceList.add(info);
                                }
                            }
                            updateGroupData(isAppend, liveServiceList);
                        } else {
                            updateGroupData(isAppend, null);
                        }
                        pullView.onRefreshComplete();

                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        pullView.onRefreshComplete();
                        if (currentPage == 1) {
                            setEmptyViewVisible(null);
                        }
                    }
                });
    }

    @Override
    public void onStartLoadingGroup(final int groupOrdinal) {
        try {
            CollectionView.InventoryGroup<ILiveService, ILiveService.ILiveInputStream>
                    group = headerGroup.getGroups().get(headerGroup.getGroupIndex(groupOrdinal));
            if (group != null) {
                final ILiveService service = group.getHeaderItem();
                if (service.getLiveInputStreamList() != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            expandableListView.onFinishLoadingGroup(groupOrdinal,
                                    service.getLiveInputStreamList());
                        }
                    }, 100);

                } else {
                    helper.getCurrentUserLiveServiceDetailsInfo(service.getServiceId(),
                            new DataRequest.DataCallback<LiveServiceRoomDetailsInfo>() {
                                @Override
                                public void onSuccess(boolean isAppend, LiveServiceRoomDetailsInfo data) {
                                    ArrayList<ILiveService.ILiveInputStream> liveInputStream =
                                            new ArrayList<ILiveService.ILiveInputStream>();
                                    if (data != null && data.getId() != 0 && data.getInput_streams() != null) {
                                        for (LiveServiceRoomDetailsInfo.LiveInputStreamsIno streamsIno : data.getInput_streams()) {
                                            liveInputStream.add(streamsIno);
                                        }
                                    }
                                    service.setLiveInputStream(liveInputStream);
                                    expandableListView.onFinishLoadingGroup(groupOrdinal,
                                            liveInputStream);
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    expandableListView.onFinishLoadingGroup(groupOrdinal,
                                            new ArrayList<ILiveService.ILiveInputStream>());
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            expandableListView.onFinishLoadingGroup(groupOrdinal,
                    new ArrayList<ILiveService.ILiveInputStream>());
        }

    }

    @Override
    public AsyncHeaderViewHolder newCollectionHeaderView(Context context, int groupOrdinal, ViewGroup parent) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.expanale_header_view, parent, false);

        return new MyHeaderViewHolder(v, groupOrdinal, expandableListView);
    }

    @Override
    public RecyclerView.ViewHolder newCollectionItemView(Context context, int groupOrdinal, ViewGroup parent) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_expanable_group_layout, parent, false);

        return new LiveServiceInputStreamItemHolder(v);
    }

    @Override
    public void bindCollectionHeaderView(Context context, AsyncHeaderViewHolder holder, int groupOrdinal, ILiveService headerItem) {
        MyHeaderViewHolder headerViewHolder = (MyHeaderViewHolder) holder;
        headerViewHolder.getTextView().setText(headerItem.getServiceTitle());
        headerViewHolder.getExpansionImageView().setImageResource(R.drawable.ic_arrow_down);
    }

    @Override
    public void bindCollectionItemView(Context context, RecyclerView.ViewHolder holder, int groupOrdinal, ILiveService.ILiveInputStream item) {
        LiveServiceInputStreamItemHolder itemHolder = (LiveServiceInputStreamItemHolder) holder;
        itemHolder.getSelectedImage().setImageResource(item.isSelected() ?
                R.drawable.icon_yugao_selected :
                R.drawable.icon_yugao_unselected);
        itemHolder.getTextView().setText(item.getInputName());
        itemHolder.getTextView().setTag(R.id.tag_live_service_item, item);
        try {
            ILiveService group = headerGroup.getGroups()
                    .get(headerGroup.getGroupIndex(groupOrdinal)).getHeaderItem();
            itemHolder.getTextView().setTag(R.id.tag_live_service_group, group);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class MyHeaderViewHolder extends AsyncHeaderViewHolder implements AsyncExpandableListView.OnGroupStateChangeListener {

        private final TextView textView;
        private final ProgressBar mProgressBar;
        private ImageView ivExpansionIndicator;

        public MyHeaderViewHolder(View v, int groupOrdinal, AsyncExpandableListView asyncExpandableListView) {
            super(v, groupOrdinal, asyncExpandableListView);
            textView = (TextView) v.findViewById(R.id.item_service_title_text);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF,
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            ivExpansionIndicator = (ImageView) v.findViewById(R.id.ivExpansionIndicator);
        }


        public TextView getTextView() {
            return textView;
        }


        public ImageView getExpansionImageView() {
            return ivExpansionIndicator;
        }

        @Override
        public void onGroupStartExpending() {
            mProgressBar.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onGroupExpanded() {
            mProgressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_up);
        }

        @Override
        public void onGroupCollapsed() {
            mProgressBar.setVisibility(View.GONE);
            ivExpansionIndicator.setVisibility(View.VISIBLE);
            ivExpansionIndicator.setImageResource(R.drawable.ic_arrow_down);

        }
    }

    public class LiveServiceInputStreamItemHolder extends RecyclerView.ViewHolder {

        private ImageView selectedImage;
        private TextView textView;

        public LiveServiceInputStreamItemHolder(View itemView) {
            super(itemView);
            selectedImage = (ImageView) itemView.findViewById(R.id.item_selected_image);
            textView = (TextView) itemView.findViewById(R.id.item_title_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object tagItem = textView.getTag(R.id.tag_live_service_item);
                    Object tagGroup = textView.getTag(R.id.tag_live_service_group);
                    if (tagItem != null && tagItem instanceof ILiveService.ILiveInputStream) {
                        ILiveService.ILiveInputStream inputStream = (ILiveService.ILiveInputStream) tagItem;
                        if (selectedInputstream != null) {
                            selectedInputstream.setSelected(false);
                        }
                        if (!inputStream.isSelected()) {
                            inputStream.setSelected(true);
                            selectedInputstream = inputStream;
                            btnStartLive.setEnabled(true);
                            if (tagGroup instanceof ILiveService) {
                                selectedLiveService = (ILiveService) tagGroup;
                            }
                        } else {
                            inputStream.setSelected(false);
                            selectedInputstream = null;
                            selectedLiveService = null;
                            btnStartLive.setEnabled(false);
                        }
                        expandableListView.updateInventory(headerGroup);
                    }
                }
            });
        }

        public ImageView getSelectedImage() {
            return selectedImage;
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
