package me.lake.librestreaming.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.Util.LogcatHelper;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.lzcms.liveroom.DrawerActivity;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.dfsx.lzcms.liveroom.view.StopLiveAlertDialog;
import me.lake.librestreaming.client.RESClient;
import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.core.listener.RESVideoChangeListener;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.Size;
import me.lake.librestreaming.sample.audiofilter.SetVolumeAudioFilter;
import me.lake.librestreaming.sample.frag.LiveRecordInfoFrag;
import me.lake.librestreaming.sample.hardfilter.extra.GPUImageCompatibleFilter;
import me.lake.librestreaming.sample.hardfilter.extra.MagicBeautyFilter;
import me.lake.librestreaming.sample.ui.AspectTextureView;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by liuwb on 2016/11/18.
 */
public class LiveRecordStreamingActivity extends DrawerActivity implements TextureView.SurfaceTextureListener,
        RESConnectionListener, RESVideoChangeListener {
    public static final String DIRECTION = "LiveRecordStreamingActivity.direction";
    public static final String SUBJECT = "LiveRecordStreamingActivity._subject";
    public static final String COVER_IMAGE_PATH = "LiveRecordStreamingActivity.cover_image_path";

    public static final String LIVE_RTMP_URL = "LiveRecordStreamingActivity.rtmp_url";

    public static final int COUNT_TIME = 1001;
    private LiveRecordInfoFrag _drawerFrag;

    private View _liveRecordView;
    private TextView _tvSpeed;

    private AspectTextureView _textureView;

    protected RESClient _resClient;
    protected RESConfig _resConfig;

    protected int _filtermode = RESConfig.FilterMode.HARD;

    protected boolean _started;

    private Handler _mainHander;

    private String _subject;

    private String _coverImagePath;

    protected SurfaceTexture _texture;
    protected int _sw;
    protected int _sh;

    private ScheduledExecutorService _scheduledExecutorService;
    private ReordTimeTask _timeTask;
    private Timer startTimer;
    private long _startTime;
    private boolean isTagStopTimeCount;

    private boolean _isHaveFrontCamera = true;

    /**
     * 是竖直方向的屏幕么.
     */
    private boolean _isScreenPortrait;

    private int _restartCount = 0;

    private int _stopLiveCount = 0;

    private String _rtmpUrl;

    private StopLiveAlertDialog _stopLiveAlertDialog;

    private boolean _isTagStop = false;

    private NotificationManager _notificationManager;

    private int currentLevel = -1;

    private LXDialog stopAlertdialog;
    private boolean noteStopDialogIsShowing;

    private boolean isLiveServiceRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        doIntent();
        super.onCreate(savedInstanceState);
        initRecord();
        if (!isLiveServiceRecord) {
            //活动直播就不去取个人直播的详情了
            getPersonalRoomInfo(true);
        }
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(_onNetworkChangeListener);
    }

    private void initRecord() {
        if (!isHasCamera()) {
            Toast.makeText(context, "没有相机", Toast.LENGTH_SHORT).show();
            return;
        }
        _isHaveFrontCamera = isHasFrontCamera();
        _textureView.setKeepScreenOn(true);
        _textureView.setSurfaceTextureListener(this);
        _resClient = new RESClient();
        _resConfig = RESConfig.obtain();
        _resConfig.setFilterMode(_filtermode);
        int defaultCameraId = _isHaveFrontCamera
                ?
                Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK;
        Size videoSize = _isScreenPortrait ? new Size(640, 360) : new Size(1280, 720);
        int bitRate = _isScreenPortrait ? 1000 * 1000 : 1700 * 1000;
        int videoFps = 25;
        LogUtils.e("TAG", "video info == " + "(" + videoSize.getWidth() + "," + videoSize.getHeight() + ")" +
                "  " + bitRate + "  " + videoFps);
        _resConfig.setTargetVideoSize(videoSize); //(720, 480)
        _resConfig.setBitRate(bitRate);//1000*1024
        _resConfig.setVideoFPS(videoFps);
        _resConfig.setRenderingMode(RESConfig.RenderingMode.OpenGLES);
        _resConfig.setDefaultCamera(defaultCameraId);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        if (_isHaveFrontCamera) {
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
        }
        int frontDirection = cameraInfo.orientation;
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        int backDirection = cameraInfo.orientation;
        if (_isScreenPortrait) {
            _resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90) | RESConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL);
            _resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
        } else {
            _resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
            _resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0) | RESConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL);
        }

        _mainHander = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    //for test
                    //                    _tvSpeed.setText("byteSpeed=" + (_resClient.getAVSpeed() / 1024) + "; \n drawFPS=" + _resClient.getDrawFrameRate() + "; \n sendFPS=" + _resClient.getSendFrameRate() + "; \n sendbufferfreepercent=" + _resClient.getSendBufferFreePercent());
                    //                    sendEmptyMessageDelayed(0, 3000);
                    if (_resClient.getSendBufferFreePercent() <= 0.05) {
                        //                        Toast.makeText(activity, "sendbuffer is full,netspeed is low!", Toast.LENGTH_SHORT).show();
                    }
                } else if (msg.what == COUNT_TIME) {
                    long span = System.currentTimeMillis() - _startTime;
                    countTime(span);
                }
            }
        };

        //        _rtmpUrl = "rtmp://live.hkstv.hk.lxdns.com:1935/live/stream123";
        prepareLibstreaming("");

        //test
        //        startLibstreaming("");
    }

    private boolean isHasFrontCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (Camera.CameraInfo.CAMERA_FACING_FRONT == info.facing) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onConfigurationChangedChangeViewVisible(Configuration newConfig) {
    }

    private boolean isHasCamera() {
        int cameraCount = Camera.getNumberOfCameras();
        int failCount = 0;
        Camera camera = null;
        for (int i = 0; i < cameraCount; i++) {
            try {
                camera = Camera.open(i);
                //                List<Camera.Size> sizeList = camera.getParameters().getSupportedVideoSizes();
                //                for (Camera.Size size : sizeList) {
                //                    Log.e("TAG", i + " ----size  == " + size.width + ", " + size.height);
                //                }
            } catch (Exception e) {
                failCount++;
            } finally {
                if (camera != null) {
                    camera.release();
                }
            }
        }
        return failCount != cameraCount;
    }

    private void doIntent() {
        RecordRoomIntentData intentData = (RecordRoomIntentData) getIntentSerializableData();
        _isScreenPortrait = intentData == null || intentData.isScreenPortrait();
        if (_isScreenPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (intentData == null) {
            return;
        }
        isLiveServiceRecord = intentData.isLiveServiceRecord();
        _subject = intentData.getSubject();
        _coverImagePath = intentData.getCoverImagePath();
        _rtmpUrl = intentData.getLiveRTMPURL();
    }


    @Override
    protected void setMainContent(FrameLayout v) {
        super.setMainContent(v);
        _liveRecordView = getLayoutInflater().inflate(R.layout.live_record_layout, null);
        v.addView(_liveRecordView);
        _textureView = (AspectTextureView) _liveRecordView.findViewById(R.id.txv_preview);
        _tvSpeed = (TextView) findViewById(R.id.tv_speed);
        _tvSpeed.setTextColor(Color.YELLOW);

        _tvSpeed.setVisibility(View.GONE);
    }

    public String getSharedWebUrl() {
        String api = isLiveServiceRecord ? "/live/activity/" : "/live/personal/";
        return AppManager.getInstance().getIApp().getMobileWebUrl() + api + getRoomId();
    }

    @Override
    protected void setDrawer(int id) {
        super.setDrawer(id);
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        _drawerFrag = (LiveRecordInfoFrag) fragmentManager.findFragmentByTag("LiveRecordStreamingActivity_drawer_frag");
        if (_drawerFrag == null) {
            _drawerFrag = LiveRecordInfoFrag.newInstance(!_isScreenPortrait);
            transaction.add(id, _drawerFrag, "LiveRecordStreamingActivity_drawer_frag");
        } else {
            transaction.show(_drawerFrag);
        }
        transaction.commit();
    }

    public String getCoverImagePath() {
        return _coverImagePath;
    }

    public void switchCamera() {
        if (!_isHaveFrontCamera) {
            Toast.makeText(this, "当前手机只有一个摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        if (_resClient != null) {
            _resClient.swapCamera();
        }
    }

    @Override
    protected void onReceiveUserGiftMessage(List<GiftMessage> message) {
        super.onReceiveUserGiftMessage(message);
        if (_drawerFrag != null) {
            _drawerFrag.doReceiveGift(message);
        }
    }

    @Override
    protected void onReceiveUserChatMessage(List<LiveMessage> message) {
        super.onReceiveUserChatMessage(message);
        if (_drawerFrag != null && message != null) {
            _drawerFrag.processMessage(message);
        }
    }


    @Override
    public void joinChatRoomStatus(ApiException error) {
        super.joinChatRoomStatus(error);
        if (error == null) {
            if (!TextUtils.isEmpty(_rtmpUrl) && !_started) {
                startLibstreaming(_rtmpUrl);
            } else {
                Log.e("TAG", "intent  no rtmp url");
                //Toast.makeText(context, "没有可用RTMP地址", Toast.LENGTH_SHORT).show();
            }
            //            requestRtmpUrl();
        }
    }

    private MagicBeautyFilter _gpuImageBeautyFilter;

    private boolean isPreparedRecord = false;

    private GPUImageCompatibleFilter _imageFilter;

    protected void prepareLibstreaming(String rtmpUrl) {
        _resConfig.setRtmpAddr(rtmpUrl);
        if (!_resClient.prepare(_resConfig)) {
            _resClient = null;
            Log.e("TAG", "prepare,failed!!");
            Toast.makeText(this, "RESClient prepare failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        isPreparedRecord = true;
        _mainHander.sendEmptyMessageDelayed(0, 3000);
        _resClient.setConnectionListener(this);
        _resClient.setVideoChangeListener(this);
        _resClient.setSoftAudioFilter(new SetVolumeAudioFilter());

        _gpuImageBeautyFilter = new MagicBeautyFilter(activity);
        setBeautyLevel(0);
        //        _resClient.setHardVideoFilter(new GPUImageCompatibleFilter<>(_gpuImageBeautyFilter));
        //        _resClient.setHardVideoFilter(new SkinBlurHardVideoFilter(0.5f));

        Size s = _resClient.getVideoSize();
        _textureView.setAspectRatio(AspectTextureView.MODE_OUTSIDE, ((double) s.getWidth()) / s.getHeight());
    }

    /**
     * 设置磨皮美颜的等级
     * 0级为不美颜，1-5位美颜等级
     *
     * @param level
     */
    public void setBeautyLevel(int level) {
        if (!isPreparedRecord) {
            LogUtils.e("TAG", "record no prepared-------------------");
            return;
        }
        if (currentLevel != level) {
            currentLevel = level;
            if (currentLevel <= 0) {
                currentLevel = 0;
                _resClient.setHardVideoFilter(null);
            } else {
                if (currentLevel >= 5) {
                    currentLevel = 5;
                }
                _gpuImageBeautyFilter.setBeautyLevel(currentLevel);
                if (_imageFilter == null) {
                    _imageFilter = new GPUImageCompatibleFilter<>(_gpuImageBeautyFilter);
                }
                _resClient.setHardVideoFilter(_imageFilter);
            }
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setBeautyFloat(float ff) {
        _gpuImageBeautyFilter.setBeautyFloat(ff);
    }

    private void startLibstreaming(String rtmpUrl) {
        _started = true;
        _isTagStop = false;
        isTagStopTimeCount = false;
        _restartCount = 0;
        _stopLiveCount = 0;
        if (!TextUtils.isEmpty(rtmpUrl)) {
            _resClient.setRtmpAddress(rtmpUrl);
        }
        _resClient.startStreaming();

        LogcatHelper.getInstance(this)
                .start();
    }

    @Override
    public void onLiveStartMessage(LiveStartMessage message) {
        super.onLiveStartMessage(message);
        _drawerFrag.doReceiveLiveStreamInfo();
    }

    @Override
    public void onExitMessage(ExitMessage message) {
        super.onExitMessage(message);
        if (_stopLiveCount > 0) {
            if (_started) {
                Log.e("TAG", "验证不过， 服务器请求停止推流");
                stopRecord(true);
                Toast.makeText(context, "验证不过， 服务器请求停止推流", Toast.LENGTH_SHORT).show();
            }
        }
        _stopLiveCount++;
    }

    @Override
    public void onLiveEndMessage(LiveEndMessage message) {
        super.onLiveEndMessage(message);
        _drawerFrag.doReceiveLiveStop();
    }

    @Override
    public void onUpdateMemberView(int memberSize, List<ChatMember> memberList) {
        if (_drawerFrag != null) {
            _drawerFrag.updateRoomMember(memberSize, memberList);
        }
    }

    @Override
    public void onUserJoinRoomMessage(UserMessage message) {
        super.onUserJoinRoomMessage(message);
        if (_drawerFrag != null && message != null && message.isSpecial()) {
            _drawerFrag.doUserJoinRoom(message);
        }
    }

    @Override
    protected void onPersonalRoomInfoUpdate(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        super.onPersonalRoomInfoUpdate(roomDetailsInfo);
        //在详情里面有推流地址，如果初始化没有设置则开始启动推流
        if (TextUtils.isEmpty(_rtmpUrl)) {
            _rtmpUrl = roomDetailsInfo.getRtmpUrl();
            if (!TextUtils.isEmpty(_rtmpUrl)) {
                if (!_started) {
                    startLibstreaming(_rtmpUrl);
                }
            } else {
                Toast.makeText(context, "没有可用的推流地址", Toast.LENGTH_SHORT).show();
            }
        }
        //设置初始化的一些数据，为以后分享等功能准备
        if (roomDetailsInfo != null) {
            _subject = roomDetailsInfo.getTitle();
            _coverImagePath = roomDetailsInfo.getCoverUrl();
        }
        if (_drawerFrag != null) {
            _drawerFrag.doUpdateRoomChannelInfo(roomDetailsInfo);
        }
    }

    @Override
    public void onUpdateOwnerInfo(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        super.onUpdateOwnerInfo(roomDetailsInfo);
        if (_drawerFrag != null) {
            _drawerFrag.updateOwner(roomDetailsInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void OnCloseActImageClick(View v) {
        _isTagStop = true;
        if (stopLiveDialog()) {
            return;
        }
        super.OnCloseActImageClick(v);
    }

    private void showNotification() {
        if (!_started) {
            return;
        }
        _notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        ///// 第二步：定义Notification
        Intent intent = new Intent(this, LiveRecordStreamingActivity.class);
        //PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("乐山直播提示")
                .setContentText("正在后台直播中...")
                .setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR;

        /////第三步：启动通知栏，第一个参数是一个通知的唯一标识
        _notificationManager.notify(0, notification);

        Toast.makeText(context, "提示，正在后台直播中，入需关闭请进入界面退出！", Toast.LENGTH_SHORT).show();
    }

    private void clearNotification() {
        if (_notificationManager != null) {
            _notificationManager.cancel(0);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        //HOME 按键出发
        showNotification();
        super.onUserLeaveHint();
    }


    @Override
    public void onBackPressed() {
        if (_drawerFrag.onBackPress()) {
            return;
        }
        _isTagStop = true;
        if (stopLiveDialog()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean stopLiveDialog() {
        if (_started) {
            if (context != null && !context.isFinishing()) {
                _stopLiveAlertDialog = new StopLiveAlertDialog(context);
                _stopLiveAlertDialog.setOnPositiveButtonClickListener(new StopLiveAlertDialog.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(StopLiveAlertDialog dialog, View v) {
                        stopRecord(false);
                        _mainHander.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                });
                _stopLiveAlertDialog.show();
                return true;
            }
        }
        return false;
    }

    private void stopRecord(boolean isUploadLog) {
        isTagStopTimeCount = true;
        stopCountTime();
        //        if (_mainHander != null) {
        //            _mainHander.removeCallbacksAndMessages(null);
        //        }
        if (_started) {
            _resClient.stopStreaming();
        }
        _started = false;

        LogcatHelper.getInstance(this)
                .stop(isUploadLog);
    }

    @Override
    protected void onDestroy() {
        stopRecord(false);
        super.onDestroy();
        //防止关闭流不顺畅
        //延时5秒清理内存
        if (_mainHander != null) {
            _mainHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (_resClient != null) {
                        _resClient.destroy();
                    }
                }
            }, 5000);
        }
        clearNotification();
        NetworkChangeManager.getInstance().
                removeOnNetworkChangeListener(_onNetworkChangeListener);
    }


    private void startCountTime() {
        if (isTagStopTimeCount) {
            return;
        }
        _startTime = System.currentTimeMillis();
        //        if (_scheduledExecutorService != null) {
        //            _scheduledExecutorService.shutdown();
        //            _scheduledExecutorService = null;
        //        }
        //        if (_timeTask == null) {
        //            _timeTask = new ReordTimeTask();
        //        }
        //        _scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //        _scheduledExecutorService.scheduleWithFixedDelay(_timeTask, 0, 1, TimeUnit.SECONDS);

        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        startTimer = new Timer("start_record_time");
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                _mainHander.sendEmptyMessage(COUNT_TIME);
            }
        }, 1000, 1000);
    }

    public void countTime(long fs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fs);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String str = "";
        String strformat = "%02d";
        String strHour = String.format(strformat, h - 8);
        String strMiute = String.format(strformat, m);
        String strSecond = String.format(strformat, s);
        str = strHour + ":" + strMiute + ":" + strSecond;
        updateTimeCnt(str);
    }

    private void stopCountTime() {
        if (_scheduledExecutorService != null) {
            _scheduledExecutorService.shutdown();
        }
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        _mainHander.removeMessages(COUNT_TIME);
        _mainHander.post(new Runnable() {
            @Override
            public void run() {
                updateTimeCnt("00:00:00");
            }
        });
    }

    public void updateTimeCnt(String time) {
        _drawerFrag.updateTime(time);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("TAG", "onSurfaceTextureAvailable -----------------");
        if (_resClient != null) {
            _resClient.startPreview(surface, width, height);
        }
        _texture = surface;
        _sw = width;
        _sh = height;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (_resClient != null) {
            _resClient.updatePreview(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (_resClient != null) {
            _resClient.stopPreview(true);
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onOpenConnectionResult(int result) {
        if (result == 0) {
            Log.e("TAG", "server IP = " + _resClient.getServerIpAddr());
            openDrawer();
            startCountTime();
        } else {
            _mainHander.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "连接服务失败", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("TAG", "open Connection fail ");
            LogcatHelper.getInstance(this).stop();
        }
        /**
         * result==0 success
         * result!=0 failed
         */
        Log.e("TAG", "open Connection == " + (result == 0));
    }

    @Override
    public void onWriteError(int errno) {
        if (errno == 9 && _restartCount < 3) {
            _resClient.stopStreaming();
            _resClient.startStreaming();
            _restartCount++;
        } else {
            stopRecord(true);
            _isTagStop = false;
            showForceStopDialog();
            //            Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
        }
        Log.e("TAG", "writeError == " + errno);
    }

    private void showForceStopDialog() {
        if (stopAlertdialog == null) {
            stopAlertdialog = new LXDialog.Builder(this)
                    .setMessage("直播数据发送失败，直播已停止!")
                    .isHiddenCancleButton(true)
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            noteStopDialogIsShowing = false;
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        stopAlertdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                noteStopDialogIsShowing = false;
            }
        });
        if (context != null && !isFinishing() && !stopAlertdialog.isShowing() && !noteStopDialogIsShowing) {
            stopAlertdialog.show();
            noteStopDialogIsShowing = true;
        }
    }

    @Override
    public void onCloseConnectionResult(int result) {
        /**
         * result==0 success
         * result!=0 failed
         */
        Log.e("TAG", "Close Connection == " + (result == 0));
        if (result == 0 && _isTagStop) {
            finish();
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        _textureView.setAspectRatio(AspectTextureView.MODE_OUTSIDE, ((double) width) / height);
    }

    private class ReordTimeTask implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            _mainHander.sendEmptyMessage(COUNT_TIME);
        }
    }

    private NetworkChangeReceiver.OnNetworkChangeListener _onNetworkChangeListener = new NetworkChangeReceiver.OnNetworkChangeListener() {
        @Override
        public void onChange(int networkType) {
            if (networkType == NetworkUtil.TYPE_NOT_CONNECTED) {
                toastMessage("网络连接已断开");
            } else if (networkType == NetworkUtil.TYPE_MOBILE) {
                toastMessage("已切换到移动网络");
            } else if (networkType == NetworkUtil.TYPE_WIFI) {
                toastMessage("已切换到WIFI网络");
            }
        }
    };

    private void toastMessage(final String text) {
        _mainHander.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
