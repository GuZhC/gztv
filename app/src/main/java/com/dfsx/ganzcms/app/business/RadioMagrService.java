package com.dfsx.ganzcms.app.business;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.dfsx.core.common.Util.IntentUtil;

/**
 * heyang  2017-4-6
 */

public class RadioMagrService extends Service {
    private static final String TAG = "RadioMagrService";
    //    private MediaPlayer mPlayer;
    public static final String NEXT_ACTION = "com.dfsx.lscms.music.NEXT_ACTION";
    public static final String PREVIOUS_ACTION = "com.dfsx.lscms.music.PREVIOUS_ACTION";

    private String mUrl;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
//        if (mPlayer == null) {
//            mPlayer = new MediaPlayer();
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                public void onPrepared(MediaPlayer paramMediaPlayer) {
//                    paramMediaPlayer.start();
//                    //更新UI
//                    Intent intent = new Intent();
//                    intent.setAction(IntentUtil.REVICE_ACTION_RADIO_MSG);
//                    sendBroadcast(intent);
//                }
//            });
//            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    Intent intent = new Intent();
//                    intent.setAction(IntentUtil.PLAY_RADIO_ERROR);
//                    sendBroadcast(intent);
//                    return false;
//                }
//            });
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        filter.addAction(IntentUtil.PAUSE_RADIO_ACTION);
        registerReceiver(dataReceiver, filter);//注册BroadcastReceiver
        return super.onStartCommand(intent, flags, startId);
    }

    DataReceiver dataReceiver = null;

    private class DataReceiver extends BroadcastReceiver {//继承自BroadcastReceiver的子类

        @Override
        public void onReceive(Context context, Intent intent) {//重写onReceive方法
            if (intent != null) {
                String act = intent.getAction();
                if (TextUtils.equals(act, IntentUtil.REVICE_ACTION_RADIO_MSG)) {
                } else if (TextUtils.equals(act, IntentUtil.PAUSE_RADIO_ACTION)) {
                    Log.v(TAG, "onReceive()======  pause");
                    pause();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
//        Toast.makeText(this, "stop media player", Toast.LENGTH_SHORT);
        if (dataReceiver != null)
            unregisterReceiver(dataReceiver);//取消注册BroadcastReceiver
//        if (mPlayer != null) {
//            mPlayer.stop();
//            mPlayer.release();
//        }
        AudioPlayMgr.getInstance().release();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String path = intent.getStringExtra("url");
                mUrl = path;
                String action = intent.getAction();
                if (action.equals(IntentUtil.PLAY_RADIO_ACTION)) {
                    Log.v(TAG, "onStart()======  start");
                    start();
                } else if (action.equals(IntentUtil.PAUSE_RADIO_ACTION)) {
                    Log.v(TAG, "onStart()======  pause");
                    pause();
                }
            } else {
                Log.v(TAG, "onStart()======  pause");
                pause();
            }
        }
    }

    private void start() {
        AudioPlayMgr.getInstance().start(mUrl, new AudioPlayMgr.OnMediaPlayListener() {
            @Override
            public void onStart(String url) {
                //更新UI
                Intent intent = new Intent();
                intent.setAction(IntentUtil.REVICE_ACTION_RADIO_MSG);
                sendBroadcast(intent);
            }

            @Override
            public void onCompletion(String url) {

            }

            @Override
            public void onError(String url) {
                Intent intent = new Intent();
                intent.setAction(IntentUtil.PLAY_RADIO_ERROR);
                sendBroadcast(intent);
            }
        });
//        mPlayer.reset();
//        try {
//            mPlayer.setDataSource(mUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, e.toString());
//        }
//        mPlayer.prepareAsync();
    }

    public void pause() {
        AudioPlayMgr.getInstance().pause();
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//        }
    }

    public void stop() {
        AudioPlayMgr.getInstance().stop();
//        if (mPlayer != null) {
//            mPlayer.stop();
//            try {
//                mPlayer.prepare();    // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
}
