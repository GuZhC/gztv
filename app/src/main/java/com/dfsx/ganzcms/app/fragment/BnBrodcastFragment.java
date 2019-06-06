package com.dfsx.ganzcms.app.fragment;

import android.animation.ObjectAnimator;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleImageView;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.MessageIntents;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.RadioMagrService;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by heyang on  2018/1/5
 * 版纳
 */
public class BnBrodcastFragment extends Fragment {
    private static final int BAR_TEXT_SIZE_SP = 14;
    private static final int ITEM_MIN_SPACE_DP = 10;
    private final String mTag = "BnBrodcastFragment";
    private final int UPDATE_SCORLL_INDEX = 0x000045;
    private MediaPlayer mPlayer;
    private SeekBar mProgress;
    private static final int SHOW_PROGRESS = 2;
    private int mCurrentBufferPercentage = 0;
    private boolean mDragging;
    AudioManager mAudioManager;
    private int mMaxVolume;
    private int mSelectItem = -1;
    private TextView mHZView, _summaryText;
    private CustomeProgressDialog dataLoading;
    ImageView mStartBtn;
    //    ImageView mDraftingbtn;
    Animation operatingAnimn = null;
    ImageView mCirclrImage;
    boolean isStartplayng = false;
    private static final long nDefaulrIndex = 2;
    long mTypeId = -1;
    ContentCmsApi mContentCMsAPi = null;
    /**
     * 当前正在播放的频道
     */
    private ContentCmsInfoEntry playingChannel;
    private Subscription radioSubscription;
    private Subscription channelSelectscription;
    private boolean isResume, isNeedStartPlay;
    ObjectAnimator objectAnimator = null;
    int pageOffset = 1, preIndex = -1;
    ViewPager _radioPager;
    RadioGroup _radioGroup;
    private boolean _isInit = false;
    private int defaultPlayCount = 0;
    private int oldSelectedPosition;
    private int currentPosition = 0;
    private TextView _nextChannelBtn;
    private int _defaultPosition = 0;
    private List<ContentCmsInfoEntry> _radioList;
    private LinearLayout hsvContent;
    private HorizontalScrollView horizontalScrollView;
    Map<String, String> mMapWeeks;
    Map<String, ArrayList<LiveEntity.LiveChannel>> findTvlist;
    private ArrayList<Integer> itemWidthList = new ArrayList<Integer>();
    TvChatFragment chatFrag;
    BnRadioBackFragment chnnelfrag;
    private LiveEntity.LiveChannel playedChannel;
    private static String[] weekDatas = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private int weekDefaultIndex = 0;


    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (getActivity() == null)
                return false;
            switch (message.what) {
                case UPDATE_SCORLL_INDEX: {
                    int pos = (int) message.obj;
//                    if (preIndex != pos)
                    {
//                        preIndex = pos;
                        if (pos == nDefaulrIndex && isResume && playingChannel != null && !isStartplayng) {
                            if (!isStartplayng) {
                                //          playChannel(playingChannel);
                            }
                        } else if (pos != nDefaulrIndex) {
                            stopPlayBack();
                            if (chnnelfrag != null)
                                chnnelfrag.clearPlay();
                        }
                    }
                }
            }
            return false;
        }
    });

    public static BnBrodcastFragment newInstance(long id) {
        BnBrodcastFragment fragment = new BnBrodcastFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", id);
        fragment.setArguments(bundel);
        return fragment;
    }

    private class DataReceiver extends BroadcastReceiver {//继承自BroadcastReceiver的子类

        @Override
        public void onReceive(Context context, Intent intent) {//重写onReceive方法
//            doubledata = intent.getDoubleExtra("data", 0);
//            tv.setText("Service的数据为:"+data);
            if (intent != null) {
                String act = intent.getAction();
                if (TextUtils.equals(act, IntentUtil.REVICE_ACTION_RADIO_MSG)) {
                    setProgress();
                    playUI(true);
//  ///                       btnRotateSelf(mDraftingbtn);
                    updatePausePlay();
//                    mAdapter.notifyDataSetChanged();
                } else if (TextUtils.equals(act, IntentUtil.PLAY_RADIO_ERROR)) {
                    isStartplayng = false;
                    updatePausePlay();
                    playUI(false);
                }
            }
        }
    }

    DataReceiver dataReceiver = null;

    public long getChannalId() {
        return playingChannel == null ? -1 : playingChannel.getId();
    }

    public long getLiveId() {
        long id = -1;
        if (playingChannel != null)
            id = playingChannel.getLiveId();
        return id;
    }

    @Override
    public void onStart() {//重写onStart方法
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        filter.addAction(IntentUtil.REVICE_ACTION_RADIO_MSG);
        filter.addAction(IntentUtil.PLAY_RADIO_ERROR);
        getActivity().registerReceiver(dataReceiver, filter);//注册BroadcastReceiver
        super.onStart();
    }

    @Override
    public void onStop() {//重写onStop方法
        getActivity().unregisterReceiver(dataReceiver);//取消注册BroadcastReceiver
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_bn_radio_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (mAdapter == null) {//因为直播和电台的Fragment之间的切换是采用的销毁重建的方式（生命周期回全走）。让adapter保持单个对象以保持数据的持久化
//            mAdapter = new ListAdapter(getActivity());
//        }
//        mList = (PullToRefreshListView) view.findViewById(R.id.listAudio);
//        mList.setOnRefreshListener(this);
//        View headLayout = LayoutInflater.from(getActivity()).inflate(R.layout.radio_header_layout, null);
//        ((ListView) mList.getRefreshableView()).addHeaderView(headLayout);
        mContentCMsAPi = new ContentCmsApi(getActivity());
//        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // to play.
//                ContentCmsEntry info = ((ViewHolder) view.getTag()).item;
//                mCurrentBufferPercentage = 0;
//                if (i > 0) i -= 1;
//                mSelectItem = --i;
//                view.setSelected(true);
//                mAdapter.notifyDataSetChanged();
//                playingChannel = info;
//                playChannel(info);
//                // view.setBackgroundResource(android.R.color.holo_blue_light);
//            }
//        });
//        if (savedInstanceState != null) {
//            mAdapter.init(savedInstanceState);
//        }
//        mList.setAdapter(mAdapter);
        operatingAnimn = AnimationUtils.loadAnimation(getActivity(), R.anim.radio_circle_rtato);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnimn.setInterpolator(lin);
        //InitCustomVideoView();

//        objectAnimator = ObjectAnimator.ofFloat(mCirclrImage, "rotation", 0.0f, 359.0f);
//        objectAnimator.setDuration(1000);
//        objectAnimator.setRepeatCount(Animation.INFINITE);
//        objectAnimator.setRepeatMode(Animation.RESTART);
//        objectAnimator.setInterpolator(new DecelerateInterpolator());

        initView(view);
//        initTopWeekData();
        initAction();
        initTabs();
        mTypeId = ColumnBasicListManager.getInstance().findIdByName("radio");
        getData(mTypeId, true, false, true);
    }

    public void initTabs() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        chnnelfrag = BnRadioBackFragment.newInstance(-1);
//        Fragment chatFrag = RadioChatFragment.newInstance(-1);
//        chatFrag = TvChatFragment.newInstance(1);
        fragments.add(chnnelfrag);
//        fragments.add(chatFrag);
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        _radioPager.setAdapter(fragmentPagerAdapter);
        _radioPager.setOnPageChangeListener(new MyOnPageChangeListener());
        _radioPager.setCurrentItem(0);
//        setSelectedTextColor(0);
    }

    public void setWeekDeault(int pos) {
        if (pos < 0 || pos >= 7)
            pos = 0;
        weekDefaultIndex = pos;
        setSelectedTextColor(pos);
    }

    public void initTopWeekData() {
        itemWidthList.clear();
        for (int i = 0; i < weekDatas.length; i++) {
            RelativeLayout layout = new RelativeLayout(getContext());
            TextView titletxt = new TextView(getContext());
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(weekDatas[i]);
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getActivity().getResources().getColor(R.color.COLOR_WHITE_NORMAL));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(getActivity(), weekDatas[i].length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(getActivity(), ITEM_MIN_SPACE_DP) + textLength;
            itemWidthList.add(itemWidth);
            hsvContent.addView(layout, itemWidth, Util.dp2px(getActivity(), 40));
            layout.setOnClickListener(new WeekDateClickListener(i));
            layout.setTag(i);
        }
        setSelectedTextColor(0);
//        setSelectedWeekColor(weekIndex);
    }

    class WeekDateClickListener implements View.OnClickListener {
        int position;

        public WeekDateClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            horizontalScrollView.smoothScrollTo(getItemLeftXPosition(position - 1), 0);
            if (position < weekDatas.length) {
                String data = weekDatas[position];
                if (chnnelfrag != null)
                    chnnelfrag.updateProgramList(data);
//                if (findTvlist != null && findTvlist.size() > 0) {
//                    String totoday = mMapWeeks.get(data);
//                    ArrayList<LiveEntity.LiveChannel> list = findTvlist.get(totoday);
//                    if (chnnelfrag != null)
//                        chnnelfrag.adapter.update(list);
////                    adapter.update(list);
//                    if (list != null && !list.isEmpty()) {
//                        if (playedChannel == null || playedChannel.url == null || TextUtils.isEmpty(playedChannel.url)) {
//                            playedChannel = list.get(0);
//                        }
//                    }
//                }
            }
            setSelectedTextColor(position);
//            setSelectedWeekColor(0);
        }
    }

    public void setSelectedTextColor(int selectedPosition) {
        currentPosition = selectedPosition;
        TextView oldSelectedText = (TextView) hsvContent.findViewWithTag(oldSelectedPosition).
                findViewById(R.id.scroll_item_text_id);
        TextView selectedText = (TextView) hsvContent.findViewWithTag(selectedPosition).
                findViewById(R.id.scroll_item_text_id);
        if (getActivity() != null) {
            oldSelectedText.setTextColor(getResources().getColor(R.color.COLOR_WHITE_NORMAL));
            selectedText.setTextColor(getResources().getColor(R.color.COLOR_WHITE));
        }
        oldSelectedPosition = selectedPosition;
    }

    public int getItemLeftXPosition(int pos) {
        int xPos = 0;
        for (int i = 0; i < pos && i < itemWidthList.size(); i++) {
            xPos += itemWidthList.get(i);
        }
        return xPos;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setSelectedTextColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "Radio_____onResume");
        isResume = true;
//        if (mAdapter != null) {
//            if (!mAdapter.isInited() && mTypeId != -1) {
//        if (!_isInit && mTypeId != -1) {
//            getData(mTypeId, true, false, true);
//        }
//            }
//        }
        if (isNeedStartPlay) {
            isNeedStartPlay = false;
//            if (!mPlayer.isPlaying()) {
//                replay();
//            }
        }
    }

//    public void setSelectedTextColor(int selectedPosition) {
//        switch (selectedPosition) {
//            case 0:
//                _radioGroup.check(R.id.bar_racio_tables);
//                break;
//            case 1:
//                _radioGroup.check(R.id.bar_racio_chat);
//                break;
//        }
//    }

    private void initAction() {
        radioSubscription = RxBus.getInstance().
                toObserverable(Intent.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (intent.getAction().equals(IntentUtil.ACTION_SCROLL_ITEM_OK)) {
                            int pos = intent.getIntExtra("pos", -1);
                            if (pos != -1) {
                                Message msg = mHandler.obtainMessage(UPDATE_SCORLL_INDEX);
                                msg.obj = pos;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                });


        channelSelectscription = RxBus.getInstance().
                toObserverable(MessageData.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageData>() {
                    @Override
                    public void call(MessageData messageData) {
                        if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_RADIO_BACK_SELECT)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
                            if (channel.isLive) {
                                if (_radioList != null && currentPosition >= 0 && currentPosition < _radioList.size()) {
                                    ContentCmsInfoEntry channel1 = _radioList.get(_defaultPosition);
                                    playBackChannel(channel1.getUrl());
                                } else {
                                    ToastUtils.toastMsgFunction(getActivity(), "音频文件无效");
                                }
                            } else {
                                if (channel != null && !TextUtils.isEmpty(channel.url)) {
//                                _channelTxt.setText(channel.channelName);
//                                playedChannel = channel;
                                    playBackChannel(channel.url);
                                } else {
                                    LSUtils.toastMsgFunction(getActivity(), "音频文件无效");
                                }
                            }
                        } else if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_RADIO_CLOSED)) {
                            stopPlayBack();
                            if (chnnelfrag != null)
                                chnnelfrag.clearPlay();
                        }
                    }
                });
    }

    //Audio MediaControl
    MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    ButtonClickListener listener;

    @Override
    public void onPause() {
        Log.e("TAG", "Radio onPause---");
        super.onPause();
        isResume = false;
    }

    public void startCirlceAim() {
        if (operatingAnimn != null)
            mCirclrImage.startAnimation(operatingAnimn);
    }

    public void stopCirclAim() {
        mCirclrImage.clearAnimation();
    }

    /**
     * 设置radio播放频道
     *
     * @param info
     */
    private void playChannel(ContentCmsInfoEntry info, boolean isUpdatetables) {
        if (info == null || info.getUrl() == null
                || TextUtils.isEmpty(info.getUrl())) {
            LSUtils.toastMsgFunction(getContext(), "音频文件无效");
            return;
        }
        if (mSelectItem == -1) mSelectItem = nprepos;
        String thumb = "";
        if (info != null && info.getThumbnail_urls() != null
                && info.getThumbnail_urls().size() > 0)
            thumb = info.getThumbnail_urls().get(0);
        Util.LoadThumebImage(mCirclrImage, thumb, null);
//        setSelectedTextColor(weekDefaultIndex);
        Log.e("radio", "playChannel  url===" + info.getUrl());
        if (isUpdatetables)
            RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_RADIO_ITEM_SELECT, null));
        mHZView.setText(info.getTitle());
        _summaryText.setText(info.getSummary());
        if (info.getNextLiveName() != null)
            _nextChannelBtn.setText(info.getNextLiveName());
        if (chatFrag != null) {
            chatFrag.getData(1);
        }
        isStartplayng = true;
        Intent intent = new Intent(getActivity(), RadioMagrService.class);
        intent.setAction(IntentUtil.PLAY_RADIO_ACTION);
        intent.putExtra("url", info.getUrl());
        getActivity().startService(intent);

/**
 if (info == null || preIndex != nDefaulrIndex) {
 return;
 }
 playingChannel = info;
 Uri url = Uri.parse(info.getUrl());
 mUrl = info.getUrl();
 //        mUrl = "http://store.ovp.wsrtv.com.cn:9090/ach2/hls/playlist.m3u8";
 try {
 LiveRadioFragment.this.setAudioUrl(mUrl);
 //setProgress();
 } catch (Exception e) {
 Log.e(LiveRadioFragment.this.mTag, e.toString());
 }
 //        setHZText(info.frequency);   **/
    }

    private void playBackChannel(String url) {
        stopPlayBack();
        Intent intent = new Intent(getActivity(), RadioMagrService.class);
        intent.setAction(IntentUtil.PLAY_RADIO_ACTION);
        intent.putExtra("url", url);
        getActivity().startService(intent);
    }

    public void stopPlayBack() {
//        if (!isStartplayng) return;
        if (getActivity() == null) return;
        isStartplayng = false;
        nprepos = mSelectItem;
        Intent intent = new Intent();
        intent.setAction(IntentUtil.PAUSE_RADIO_ACTION);
        getActivity().sendBroadcast(intent);
        updatePausePlay();
        playUI(false);
        mSelectItem = -1;
//        mAdapter.notifyDataSetChanged();
    }

    public void initView(View view) {
        hsvContent = (LinearLayout) view.findViewById(R.id.hsv_content);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.hsv_view);
        mHZView = (TextView) view.findViewById(R.id.HZ_TextView);
        _summaryText = (TextView) view.findViewById(R.id.HZ_describr_Text);
        _radioPager = (ViewPager) view.findViewById(R.id.raido_pager);
        _radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_bar);
        listener = new ButtonClickListener();
        mCirclrImage = (ImageView) view.findViewById(R.id.fm_ciru_lay);
        mStartBtn = (ImageView) view.findViewById(R.id.radio_plays_btn);
        mStartBtn.setOnClickListener(listener);
        _nextChannelBtn = (TextView) view.findViewById(R.id.fm_ciruss_lay);
        _nextChannelBtn.setOnClickListener(listener);
//        mDraftingbtn = (ImageView) view.findViewById(R.id.fm_header_lay);
//        mDraftingbtn.setOnClickListener(listener);
//        Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable
//                (R.drawable.head_fm)).getBitmap();
//        Bitmap mp = rotateBitmap(mBitmap, -30);
//        mDraftingbtn.setImageBitmap(mp);
        mAudioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mProgress = (SeekBar) view.findViewById(R.id.radio_seek_bar);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(mMaxVolume);
        }
        _radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int a = 0;
                switch (checkedId) {
                    case R.id.bar_racio_tables:
                        _radioPager.setCurrentItem(0);
                        break;
                    case R.id.bar_racio_chat:
                        _radioPager.setCurrentItem(1);
                        break;
                }
            }
        });
//        initMediaPlayer();
    }

    public void playUI(boolean flag) {
        isStartplayng = flag;
//        btnRotateSelf(null);
    }

    public void btnRotateSelf(View view) {
        float rotatye = -30;
        float ogrtota = -15;
        if (isStartplayng) {
            ogrtota = -30;
            rotatye = -15;
        }
        //旋转动画，设置旋转参考系
//        RotateAnimation ra = new RotateAnimation(RotateAnimation.RELATIVE_TO_SELF, rotatye,
//                50, 0.5F,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5F);

//        RotateAnimation ra = new RotateAnimation(ogrtota, rotatye,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5F,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5F);
//
        RotateAnimation ra = new RotateAnimation(ogrtota, rotatye,
                0, 0.5F,
                0, 0.5F);


        ra.setFillAfter(true);
        ra.setDuration(1000);
        ra.setInterpolator(new AccelerateInterpolator()); // 设置插入器
//        view.startAnimation(ra);

        if (isStartplayng) {
            startCirlceAim();
        } else
            stopCirclAim();

//        updatePausePlay();

//        mDraftingbtn.setDrawingCacheEnabled(true);
//        Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable
//                (R.drawable.head_fm)).getBitmap();
//        Bitmap mp = rotateBitmap(mBitmap, -30);
//        Bitmap mp = rotateBitmap(mBitmap, 10);
//        mDraftingbtn.setImageBitmap(mp);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setTranslate(200, 56);
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    private void initMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer paramMediaPlayer) {
                    paramMediaPlayer.start();
                    setProgress();
                    playUI(true);
//  ///                       btnRotateSelf(mDraftingbtn);
                    updatePausePlay();
                }
            });
        }
    }

    private void setProgress() {
        if (mPlayer == null || mDragging) {
            return;
        }
        if (mProgress != null) {
            // use long to avoid overflow
            long pos = 7;
            mProgress.setProgress((int) pos);
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            //show(3600000);
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mPlayer == null) {
                return;
            }
            if (!fromuser) {
                return;
            }
            //float curVol = progress * 1.0f / (mMaxVolume * 1.0f);
            // curVol = curVol*1.0f;
            int curVol = progress;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVol, 0);
            //mPlayer.seekTo( (int) newposition);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
//            setProgress();
            updatePausePlay();
            //show(sDefaultTimeout);
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private int nprepos = 0;

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    case R.id.radio_plays_btn: {
                        if (isStartplayng) {
                            stopPlayBack();
                        } else {
                            playChannel(playingChannel, false);
                        }
                        break;
                    }
                    case R.id.fm_header_lay: {
                        break;
                    }
                    case R.id.fm_ciruss_lay: {
                        if (_radioList != null && !_radioList.isEmpty()) {
                            int pos = _defaultPosition;
                            pos++;
                            if (pos < _radioList.size()) {
//                                _defaultPosition = pos;
//                                playingChannel = _radioList.get(pos);
//                                playChannel(playingChannel);
                            } else {
                                pos = 0;
//                                LSUtils.toastMsgFunction(getActivity(), "已经是最后一个频道了！");
                            }
                            _defaultPosition = pos;
                            playingChannel = _radioList.get(pos);
                            setNextChannelName();
                            playChannel(playingChannel, true);
                        }
                    }
                    break;
                }
            } catch (Exception e) {//抛出异常
                Log.e(mTag, e.toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "Radio--- onDestroy  ");
        stopPlayBack();
        if (radioSubscription != null) {
            radioSubscription.unsubscribe();
        }
        if (channelSelectscription != null) {
            channelSelectscription.unsubscribe();
        }
    }

    public void updatePausePlay() {
        if (getActivity() == null) return;
        if (isStartplayng) {
            mStartBtn.setImageResource(R.drawable.radio_pause);
        } else {
            mStartBtn.setImageResource(R.drawable.radio_play);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (mAdapter != null)
        //           mAdapter.saveInstanceState(outState);
    }

    public void startPlay() {
        isNeedStartPlay = true;
    }

    private void replay() {
//        boolean isLeagle = mAdapter != null && mAdapter.getData() != null && defaultPlayCount >= 0 &&
//                defaultPlayCount < mAdapter.getData().size();
//        if (isLeagle) {
//            playChannel(mAdapter.getData().get(defaultPlayCount));
//        }
    }

    class MyAdapter extends BaseListViewAdapter<LiveEntity.LiveChannel> {
        private int selectedPosition = -1;// 选中的位置

        public MyAdapter(Context context) {
            super(context);
        }

        public void setSelectPostion(int position) {
            this.selectedPosition = position;
            notifyDataSetInvalidated();
        }

        public void update(ArrayList<LiveEntity.LiveChannel> data) {
            list = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.radio_program_item_list;
        }

        @Override
        public void setItemViewData(BaseViewHodler viewHodler, int position) {
            View body = viewHodler.getView(R.id.tv_body_layout);
            View selectline = viewHodler.getView(R.id.radio_sel_line);
            TextView timeView = viewHodler.getView(R.id.tv_item_time);
            TextView titleView = viewHodler.getView(R.id.tv_item_title_tx);
            ImageView status = viewHodler.getView(R.id.proram_status_mask);
            ImageView operartxr = viewHodler.getView(R.id.radio_play_mask);
            LiveEntity.LiveChannel channe = list.get(position);
            titleView.setTextColor(getResources().getColor(R.color.tv_name_selected));
            if (channe.isPlayback) {
                operartxr.setVisibility(View.VISIBLE);
            } else {
                operartxr.setVisibility(View.GONE);
            }
            if (!channe.isPlayback) {
                if (channe.isLive) {
                    status.setImageResource(R.drawable.radio_program_select);
                    operartxr.setVisibility(View.VISIBLE);
                } else {
                    status.setImageResource(R.drawable.tv_program_select);
                    operartxr.setVisibility(View.GONE);
                }
            }
            if (selectedPosition == position) {
                selectline.setVisibility(View.VISIBLE);
            } else {
                selectline.setVisibility(View.GONE);
            }
            timeView.setText(list.get(position).creaTime);
            titleView.setText(list.get(position).channelName);
        }

        private void whenClicked(int position) {
            Toast.makeText(context, "position " + position + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    void assembleNodes(JSONObject jsonObject, ArrayList<LiveEntity.LiveChannel> liveChannelsAarry) {
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = (JSONObject) result.get(i);
                    LiveEntity.LiveChannel channel = new LiveEntity.LiveChannel();
                    channel.channelID = item.getInt("nid");
                    channel.channelName = item.getString("node_title");
                    channel.content = item.optString("field_live_inrto");
                    // JSONObject detail = item.getJSONObject("field_node_tv_app");
                    // channel.content = detail.getString("type");
                    //channel.frequency = "91.5HZ";
                    channel.url = item.getString("field_live_audio_app");
                    liveChannelsAarry.add(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileId() {
//        String defaultId = "live_radio_default";
        if (App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null) {
            return mTag + App.getInstance().getUser().getUser().getId();
        }
        return mTag;
    }

    private String getFileName() {
        return mTag;
    }

    private DataFileCacheManager<ArrayList<ContentCmsEntry>> dataRequest =
            new DataFileCacheManager<ArrayList<ContentCmsEntry>>(App.getInstance().getApplicationContext(),
                    getFileId(), App.getInstance().getPackageName() + getFileName()) {
                @Override
                public ArrayList<ContentCmsEntry> jsonToBean(JSONObject json) {
                    ArrayList<ContentCmsEntry> dlist = null;
                    try {
                        if (json != null && !TextUtils.isEmpty(json.toString())) {
                            JSONArray attr = json.optJSONArray("data");
                            if (attr != null && attr.length() > 0) {
                                dlist = new ArrayList<ContentCmsEntry>();
                                for (int i = 0; i < attr.length(); i++) {
                                    JSONObject obj = (JSONObject) attr.get(i);
                                    ContentCmsEntry entry = new Gson().fromJson(obj.toString(), ContentCmsEntry.class);
                                    dlist.add(entry);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dlist;
                }
            };

    private void getData(long baseId, boolean bNext, boolean bAddTail, boolean showProcessBar) {
        if (showProcessBar) {
            //         dataLoading = CustomeProgressDialog.show(getActivity(), "");
        }
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + mTypeId + "/contents?";
        url += "page=" + pageOffset;
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setToken(App.getInstance().getCurrentToken()).build();
        dataRequest.getData(httpParams, bAddTail).
                setCallback(new DataRequest.DataCallback<List<ContentCmsEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, final List<ContentCmsEntry> liveChannelsAarry) {
                        if (liveChannelsAarry != null && !liveChannelsAarry.isEmpty()) {
                            Observable.from(liveChannelsAarry)
                                    .subscribeOn(Schedulers.io())
                                    .map(new Func1<ContentCmsEntry, ContentCmsInfoEntry>() {
                                        @Override
                                        public ContentCmsInfoEntry call(ContentCmsEntry topicalEntry) {
                                            ContentCmsInfoEntry entry = mContentCMsAPi.getContentCmsInfo(topicalEntry.getId());
//                                            topicalEntry.setUrl(entry.getUrl());
                                            return entry;
                                        }
                                    })
                                    .toList()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<ContentCmsInfoEntry>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            if ((dataLoading != null) && (dataLoading.isShowing()))
                                                dataLoading.dismiss();
//                                            if (mList != null) {
//                                                mList.onRefreshComplete();
//                                            }
                                        }

                                        @Override
                                        public void onNext(List<ContentCmsInfoEntry> data) {
                                            if (data != null && !data.isEmpty()) {
                                                _radioList = data;
                                                playingChannel = _radioList.get(0);
                                                if (playingChannel != null) {
                                                    String thumb = getChannelImageThumb(playingChannel);
                                                    Util.LoadThumebImage(mCirclrImage, thumb, null);
                                                    mHZView.setText(playingChannel.getTitle());
                                                    _summaryText.setText(playingChannel.getSummary());
                                                    setNextChannelName();
                                                    if (chatFrag != null)
                                                        chatFrag.getData(1);
                                                    chnnelfrag.initData();
                                                }
                                            }
//                                            if (defaultPlayCount >= 0 && defaultPlayCount < liveChannelsAarry.size()) {
//                                                setDefaultVideoUrl(liveChannelsAarry.get(defaultPlayCount));
//                                                if (preIndex == nDefaulrIndex)
//                                                    playChannel(playingChannel);
//                                            }
                                            if ((dataLoading != null) && (dataLoading.isShowing()))
                                                dataLoading.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
//                        if (mList != null) {
//                            mList.onRefreshComplete();
//                        }
                        if ((dataLoading != null) && (dataLoading.isShowing()))
                            dataLoading.dismiss();
                    }
                });
    }

    public void setNextChannelName() {
        String chanelName = "";
        if (_radioList == null || _radioList.isEmpty()) return;

        if (_defaultPosition >= 0) {
            int index = _defaultPosition;
            int next = ++index;
            if (next >= _radioList.size())
                next = 0;
            ContentCmsInfoEntry nextChannel = _radioList.get(next);
            chanelName = nextChannel.getTitle();
        }
        if (!TextUtils.isEmpty(chanelName))
            _nextChannelBtn.setText(chanelName);
    }

    public String getChannelImageThumb(ContentCmsInfoEntry entry) {
        String thumb = "";
        if (entry != null && entry.getThumbnail_urls() != null
                && entry.getThumbnail_urls().size() > 0)
            thumb = entry.getThumbnail_urls().get(0);
        return thumb;
    }

    /**
     * 设置默认播放数据且让他不重复播放
     *
     * @param channel
     */
    private void setDefaultVideoUrl(ContentCmsInfoEntry channel) {
//        if (channel != null && !isPlaying() && (playingChannel == null ||
//                channel.getId() != playingChannel.getId())) {
        playingChannel = channel;
        mCurrentBufferPercentage = 0;
//        mSelectItem = defaultPlayCount;
//        mAdapter.notifyDataSetChanged();
//        }
    }

}