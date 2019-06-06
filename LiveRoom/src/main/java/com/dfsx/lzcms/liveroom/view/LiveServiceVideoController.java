package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.videoijkplayer.media.IMediaController;

import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 * 控制台实现IVideoMultilineVideoPlayerApi接口。只实现UI显示控制
 */
public class LiveServiceVideoController implements IMediaController, IVideoMultilineVideoPlayerApi {

    public static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 5000;

    protected Context context;
    protected IVideoMultilineVideoPlayerApi videoPlayer;
    protected ViewGroup controllerContainer;
    private ListView multilineListView;
    private ImageView btnControllPlay;
    private ImageView btnControllSwitch;
    private FrameLayout videoTimeViewContainer;
    private ImageView backImageView;
    private ImageView moreImageView;

    private TextView videoTitleTextView;

    protected boolean isShow = true;
    private View controllerRootView;
    private boolean isFullScreen = false;

    private MultilineAdapter multilineAdapter;
    private OnLiveServiceVideoEventClickListener listener;

    private boolean isMultilineCouldClick = true;

    private OnVideoMultilinePlayChangeListener multilinePlayChangeListener;

    protected Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleHandlerMessage(msg);
        }
    };


    public LiveServiceVideoController(Context context, IVideoMultilineVideoPlayerApi videoPlayer, ViewGroup controllerContainer) {
        this.context = context;
        this.videoPlayer = videoPlayer;
        this.controllerContainer = controllerContainer;
        init();
    }

    private void init() {
        controllerRootView = LayoutInflater.from(context)
                .inflate(R.layout.service_video_controller_layout, null);
        controllerContainer.addView(controllerRootView);
        multilineListView = (ListView) findViewByid(R.id.video_url_listView);
        btnControllPlay = (ImageView) findViewByid(R.id.btn_play_controller);
        videoTitleTextView = (TextView) findViewByid(R.id.video_title_text);
        btnControllSwitch = (ImageView) findViewByid(R.id.btn_video_switch_screen);
        videoTimeViewContainer = (FrameLayout) findViewByid(R.id.video_time_layout_container);
        backImageView = (ImageView) findViewByid(R.id.image_back);
        moreImageView = (ImageView) findViewByid(R.id.image_more);

        onSetVideoTimeView(videoTimeViewContainer);

        multilineAdapter = new MultilineAdapter(context);
        multilineListView.setAdapter(multilineAdapter);

        initAction();
    }

    public void setVideoTitle(String title) {
        videoTitleTextView.setText(title);
    }

    protected void initAction() {
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    videoPlayer.switchScreen(!isFullScreen);
                } else {
                    if (listener != null) {
                        listener.onFinishClick(v);
                    }
                }
            }
        });

        moreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoreClick(v);
                }
            }
        });
        multilineListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isMultilineCouldClick) {
                    position = position - multilineListView.getHeaderViewsCount();
                    if (multilineAdapter != null &&
                            multilineAdapter.getData() != null &&
                            position < multilineAdapter.getCount() &&
                            position >= 0) {
                        IMultilineVideo video = multilineAdapter.getData().get(position);
                        if (video != null && video.getVideoUrlList() != null
                                && !video.getVideoUrlList().isEmpty()) {
                            if (!video.isSelected()) {
                                videoPlayer.selectedMultiline(video);
                                if (multilinePlayChangeListener != null) {
                                    //设置线路切换的回调
                                    multilinePlayChangeListener.onVideoMultilinePlayChanged(video);
                                }
                            } else {
                                //Toast.makeText(context, "正在播放当前线路", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "线路不可用", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btnControllPlay.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                boolean isPlay = videoPlayer.isPlaying();
                setBtnControllPlayImage(isPlay);
                if (isPlay) {
                    videoPlayer.pause();
                } else {
                    if (videoPlayer.isInPlayBackStatus()) {
                        videoPlayer.start();
                    } else {
                        videoPlayer.restart();
                    }
                }
            }
        });
        btnControllSwitch.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                videoPlayer.switchScreen(isFullScreen);
            }
        });
    }

    /**
     * 视屏切换屏幕之后调用
     *
     * @param isFull
     */
    private void setSwitchImage(boolean isFull) {
        isFullScreen = isFull;
        btnControllSwitch.setImageResource(isFull ? R.drawable.icon_service_video_exit_full :
                R.drawable.icon_service_video_full);
    }

    private void setBtnControllPlayImage(boolean isPlay) {
        btnControllPlay.setImageResource(isPlay ? R.drawable.icon_service_video_pause :
                R.drawable.icon_service_video_play);
    }

    public void setMultilineClickable(boolean isClick) {
        this.isMultilineCouldClick = isClick;
    }

    protected void onSetVideoTimeView(FrameLayout videoTimeViewContainer) {

    }

    protected void handleHandlerMessage(Message msg) {
        if (msg.what == SET_VIEW_HIDE) {
            isShow = false;
            controllerRootView.setVisibility(View.GONE);
        }
    }

    private View findViewByid(int id) {
        return controllerContainer.findViewById(id);
    }

    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(SET_VIEW_HIDE);
            controllerRootView.setVisibility(View.GONE);
        }
        isShow = false;
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {

    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        isShow = true;
        controllerRootView.setVisibility(View.VISIBLE);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {

    }

    @Override
    public boolean isPlaying() {
        return videoPlayer.isPlaying();
    }

    @Override
    public void switchScreen(boolean isFull) {
        setSwitchImage(isFull);
        moreImageView.setVisibility(isFull ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean isInPlayBackStatus() {
        return videoPlayer.isInPlayBackStatus();
    }

    @Override
    public long getCurrentPosition() {
        return videoPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return videoPlayer.getDuration();
    }

    @Override
    public int getBufferPercentage() {
        return videoPlayer.getBufferPercentage();
    }

    @Override
    public void seekTo(int time) {
    }


    @Override
    public void start() {
        setBtnControllPlayImage(true);
    }

    @Override
    public void restart() {
        setBtnControllPlayImage(true);
    }

    @Override
    public void pause() {
        setBtnControllPlayImage(false);
    }

    @Override
    public void stop() {
        setBtnControllPlayImage(false);
    }

    @Override
    public void release() {
        setBtnControllPlayImage(false);
    }

    @Override
    public void selectedMultiline(IMultilineVideo multilineVideo) {
        if (multilineVideo != null) {
            List<IMultilineVideo> list = multilineAdapter.getData();
            if (list != null && !list.isEmpty()) {
                for (IMultilineVideo item : list) {
                    if (item.getId() == multilineVideo.getId()) {
                        item.setSelected(true);
                    } else {
                        item.setSelected(false);
                    }
                }
                multilineAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setMultilineList(List<IMultilineVideo> multilineVideoList) {
        if (multilineAdapter != null) {
            multilineAdapter.update(multilineVideoList, false);
            if (multilineAdapter.getCount() <= 1) {
                multilineListView.setVisibility(View.GONE);
            } else {
                multilineListView.setVisibility(View.VISIBLE);
            }
        }
    }

    public List<IMultilineVideo> getMultilineList() {
        return multilineAdapter != null ? multilineAdapter.getData() : null;
    }

    public void updateMultilineView() {
        if (multilineAdapter != null) {
            multilineAdapter.notifyDataSetChanged();
        }
    }

    public void setOnVideoMultilinePlayChangeListener(OnVideoMultilinePlayChangeListener l) {
        this.multilinePlayChangeListener = l;
    }

    public void setOnLiveServiceVideoEventClickListener(OnLiveServiceVideoEventClickListener l) {
        this.listener = l;
    }

    class MultilineAdapter extends BaseListViewAdapter<IMultilineVideo> {

        public MultilineAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_item_multiline_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            View itemView = holder.getView(R.id.item_multiline_view);
            TextView title = holder.getView(R.id.item_multiline_title);
            TextView name = holder.getView(R.id.item_multiline_name);
            IMultilineVideo multilineVideo = list.get(position);
            title.setText(multilineVideo.getLineTitle() + position);
            name.setText(multilineVideo.getName());
            if (multilineVideo.isSelected()) {
                itemView.setBackgroundResource(R.drawable.bg_multiline_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_multiline_unselected);
            }
        }
    }
}
