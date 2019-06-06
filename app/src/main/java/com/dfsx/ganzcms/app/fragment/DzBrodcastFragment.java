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
import com.dfsx.core.common.Util.Util;
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
import com.dfsx.ganzcms.app.util.UtilHelp;
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

/**
 * Created by heyang on 2016/10/11.
 */
public class DzBrodcastFragment extends Fragment {
    private final String mTag = "LiveRadioFragment";
    private final int UPDATE_SCORLL_INDEX = 0x000045;
    private final int NETWORK_BUSY = 12;
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
    CircleImageView mCirclrImage;
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
    private TextView _nextChannelBtn;
    private int _defaultPosition = 0;
    private List<ContentCmsInfoEntry> _radioList;
    TvChatFragment chatFrag;
    RadioTablesFragment chnnelfrag;


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

    public static DzBrodcastFragment newInstance(long id) {
        DzBrodcastFragment fragment = new DzBrodcastFragment();
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
//        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.frag_dz_radio, container, false);
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
        initAction();
        initTabs();
        mTypeId = ColumnBasicListManager.getInstance().findIdByName("radio");
//        List<ColumnCmsEntry> dlist = App.getInstance().getmHeaderCmsList();
//        if (dlist != null && !dlist.isEmpty()) {
//            for (ColumnCmsEntry entry : dlist) {
//                if (TextUtils.equals("广播", entry.getName().toString().trim())) {
//                    mTypeId = entry.getId();
//                    break;
//                }
//            }
//        }
        getData(mTypeId, true, false, true);
    }

    public void initTabs() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        chnnelfrag = RadioTablesFragment.newInstance(-1);
//        Fragment chatFrag = RadioChatFragment.newInstance(-1);
        chatFrag = TvChatFragment.newInstance(1);
        fragments.add(chnnelfrag);
        fragments.add(chatFrag);
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        _radioPager.setAdapter(fragmentPagerAdapter);
        _radioPager.setOnPageChangeListener(new MyOnPageChangeListener());
        _radioPager.setCurrentItem(0);
//        setSelectedTextColor(0);
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

    public void setSelectedTextColor(int selectedPosition) {
        switch (selectedPosition) {
            case 0:
                _radioGroup.check(R.id.bar_racio_tables);
                break;
            case 1:
                _radioGroup.check(R.id.bar_racio_chat);
                break;
        }
    }

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
                            if (channel != null && !TextUtils.isEmpty(channel.url)) {
//                                _channelTxt.setText(channel.channelName);
//                                playedChannel = channel;
                                playBackChannel(channel.url);
                            } else {
                                LSUtils.toastMsgFunction(getActivity(), "播放音频文件无效");
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
        if (info == null) {
            LSUtils.toastMsgFunction(getContext(), "数据不存在");
            return;
        }
        if (mSelectItem == -1) mSelectItem = nprepos;
        String thumb = "";
        if (info != null && info.getThumbnail_urls() != null
                && info.getThumbnail_urls().size() > 0)
            thumb = info.getThumbnail_urls().get(0);
        Util.LoadThumebImage(mCirclrImage, thumb, null);
        if (isUpdatetables)
            RxBus.getInstance().post(new MessageData<LiveEntity.LiveChannel>(MessageIntents.RX_ITME_RADIO_ITEM_SELECT, null));
        mHZView.setText(info.getTitle());
        _summaryText.setText(info.getSummary());
//        if (info.getNextLiveName() != null)
//            _nextChannelBtn.setText(info.getNextLiveName());
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
        mHZView = (TextView) view.findViewById(R.id.HZ_TextView);
        _summaryText = (TextView) view.findViewById(R.id.HZ_describr_Text);
        _radioPager = (ViewPager) view.findViewById(R.id.raido_pager);
        _radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_bar);
        listener = new ButtonClickListener();
        mCirclrImage = (CircleImageView) view.findViewById(R.id.fm_ciru_lay);
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
        btnRotateSelf(null);
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

    public void setNextChannelName() {
        String chanelName = "";
        if (_radioList == null || _radioList.isEmpty()) return;

        if (_defaultPosition >= 0) {
            int index=_defaultPosition;
            int next = ++index;
            if (next >= _radioList.size())
                next = 0;
            ContentCmsInfoEntry nextChannel = _radioList.get(next);
            chanelName = nextChannel.getTitle();
        }
        if(!TextUtils.isEmpty(chanelName))
            _nextChannelBtn.setText(chanelName);
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

    protected class ListAdapter extends BaseAdapter implements View.OnClickListener {
        private final String STATE_LIST = "ListAdapter.mlist";
        private LayoutInflater mLayoutInflater = null;
        private ArrayList<ContentCmsEntry> mList = new ArrayList<ContentCmsEntry>();
        boolean mbInit = false;

        public ListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mbInit = false;

        }

        public void SetInitStatus(boolean flag) {
            mbInit = flag;
        }

        public boolean isInited() {
            return mbInit;
        }

        public long getMinId() {
            return mList.isEmpty() ? -1 : mList.get(mList.size() - 1).getId();
        }

        public long getMaxId() {
            return mList.isEmpty() ? -1 : mList.get(0).getId();
        }

        public void update(ArrayList<ContentCmsEntry> data, boolean bAddTail) {

            if (bAddTail)
                mList.addAll(data);
            else
                mList = data;

            mbInit = true;
            notifyDataSetChanged();
        }

        public ArrayList<ContentCmsEntry> getData() {
            return mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.radio_item_list, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.radio_title_tx);
                viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.radio_des_tx);
                viewHolder.thumb = (ImageView) convertView.findViewById(R.id.cradio_lasssb_img);
                viewHolder.favoriteImageView = (ImageView) convertView.findViewById(R.id.cradio_lab_img);
                viewHolder.selectImage = (ImageView) convertView.findViewById(R.id.cradio_lab_img);
                viewHolder.item = mList.get(position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item = mList.get(position);
            viewHolder.pos = position;
            viewHolder.titleTextView.setText(viewHolder.item.getTitle());
            viewHolder.contentTextView.setText(viewHolder.item.getSummary());
            if (position == mSelectItem)
                viewHolder.selectImage.setVisibility(View.VISIBLE);
            else
                viewHolder.selectImage.setVisibility(View.INVISIBLE);
//                convertView.setBackgroundResource(android.R.color.white);
            if (viewHolder.item.getThumbnail_urls() != null && viewHolder.item.getThumbnail_urls().size() > 0) {
                String s = viewHolder.item.getThumbnail_urls().get(0);
                String imageUrl = UtilHelp.getImagePath(s);
                Util.LoadThumebImage(viewHolder.thumb, s, null);
            } else {
                UtilHelp.LoadImageFormUrl(viewHolder.thumb, "", null);
            }
            return convertView;
        }

        @Override
        public void onClick(View view) {
        }

    }

    protected static class ViewHolder {
        public ContentCmsEntry item;
        public int pos;
        public TextView contentTextView;
        public TextView titleTextView;
        public ImageView shareImageView;
        public ImageView favoriteImageView;
        public ImageView selectImage;
        public ImageView thumb;
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
        String defaultId = "live_radio_default";
        if (App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null) {
            return defaultId + App.getInstance().getUser().getUser().getId();
        }
        return defaultId;
    }

    private String getFileName() {
        return "DzBrodcastFragment";
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
//        String url = App.getInstance().getmSession().makeUrl("services/audio_live.json");
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
//                                                    if (playingChannel.getNextLiveName() != null)
//                                                        _nextChannelBtn.setText(playingChannel.getNextLiveName());
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