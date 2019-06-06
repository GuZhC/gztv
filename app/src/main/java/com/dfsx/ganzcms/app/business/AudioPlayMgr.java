package com.dfsx.ganzcms.app.business;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.dfsx.ganzcms.app.App;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


/**
 * authpr  heyang 2017-4-12
 * 音频播放类
 */

public class AudioPlayMgr {
    private static final String TAG = AudioPlayMgr.class.getSimpleName();
    private static final int MSG_WHAT_PLAY = 1000;
    private static AudioPlayMgr mChatPlayer = null;
    private KSYMediaPlayer mPlayer = null;
    private String mCurrentUrl = "";
    private OnMediaPlayListener mMediaPlayListener = null;
    private Handler mHandler = null;
    Callback downloadCallback = new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_PLAY) {
                if (msg.obj instanceof File) {
                    File localFile = (File) msg.obj;
                    playFromLocal(localFile);
                }
            }
            return false;
        }
    };

    public static AudioPlayMgr getInstance() {
        if (mChatPlayer == null) {
            synchronized (AudioPlayMgr.class) {
                if (mChatPlayer == null) {
                    mChatPlayer = new AudioPlayMgr();
                }
            }
        }
        return mChatPlayer;
    }

    public interface OnMediaPlayListener {

        void onStart(String url);

        void onCompletion(String url);

        void onError(String url);
    }

    public void init() {
        mPlayer = new KSYMediaPlayer.Builder(App.getInstance().getBaseContext()).build();
        mPlayer.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        mPlayer.setBufferTimeMax(1);
        mHandler = new Handler(downloadCallback);
    }

    public void play(final String url, final OnMediaPlayListener listener) {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            if (mMediaPlayListener != null) {
                mMediaPlayListener.onCompletion(mCurrentUrl);
            }
        }
        mCurrentUrl = url;
        mMediaPlayListener = listener;
        mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (mMediaPlayListener != null) {
                    mMediaPlayListener.onCompletion(mCurrentUrl);
                }
            }
        });

        mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mMediaPlayListener != null) {
                    listener.onError(mCurrentUrl);
                }
                return false;
            }
        });

        /**
         if (!FileUtil.isCanUseSDCard()) {
         UIUtils.toastMsgFromResource(XManager.getManager().getApplication(), R.string.sdcard_err);
         return;
         }

         String wholeUrl = Utils.getWholeUrl(mCurrentUrl, XConfig.FILE_SCHEME);
         if (wholeUrl.startsWith(XConfig.FILE_SCHEME)) {
         playFromLocal(wholeUrl);
         return;
         }

         File localDir = new File(XConfig.DIR_CHAT_VOICE_CACHE);
         if (!localDir.exists()) {
         localDir.mkdirs();
         }
         final File localFile = new File(localDir, mCurrentUrl + ".amr");
         if (localFile.exists()) {
         playFromLocal(localFile);
         } else {
         playFromNetwork(mCurrentUrl, localFile);
         }  **/
    }

    public void start(String url, final OnMediaPlayListener listener) {
        if (mPlayer == null) {
            init();
//            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            if (mMediaPlayListener != null) {
                mMediaPlayListener.onCompletion(mCurrentUrl);
            }
        }
        mCurrentUrl = url;
        mMediaPlayListener = listener;
        mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (mMediaPlayListener != null) {
                    mMediaPlayListener.onCompletion(mCurrentUrl);
                }
            }
        });
        mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mMediaPlayListener != null) {
                    listener.onError(mCurrentUrl);
                }
                return false;
            }
        });
        playFromLocal(mCurrentUrl);
    }

    public void seekTo(int progress) {
        if (mPlayer == null) {
            return;
        }
        mPlayer.seekTo(progress);
    }

    /*
    *  得到当前长度
     */
    public long getCurrentPosition() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getCurrentPosition();
    }

    /**
     * 得到总长度
     */
    public long getDuration() {
        if (mPlayer == null) {
            return 0;
        }
        return mPlayer.getDuration();
    }

    /**
     * 得到player
     */
    public KSYMediaPlayer getPlayer() {
        return mPlayer;
    }

    /**
     * 判断是再拨
     */

    public boolean isPlaying() {
        if (mPlayer == null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    public void stop() {
        if (mPlayer != null && isPlaying()) {
            mPlayer.stop();
            if (mMediaPlayListener != null) {
                mMediaPlayListener.onCompletion(mCurrentUrl);
            }
        }
    }

    public void pause() {
        if (mPlayer != null && isPlaying()) {
            mPlayer.pause();
            if (mMediaPlayListener != null) {
                mMediaPlayListener.onError(mCurrentUrl);
            }
        }
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            mHandler.removeMessages(MSG_WHAT_PLAY);
            if (mMediaPlayListener != null) {
                mMediaPlayListener.onCompletion(mCurrentUrl);
            }
        }
    }

    private void playFromNetwork(final String source, final File localFile) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                int count;
                try {
                    URL url = new URL(source);
                    URLConnection conn = url.openConnection();
                    conn.connect();

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(localFile);

                    byte data[] = new byte[1024];

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_WHAT_PLAY;
                msg.obj = localFile;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void playFromLocal(File localFile) {
        if (mPlayer == null) {
            return;
        }
        try {
            Uri uri = Uri.fromFile(localFile);
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mPlayer.setDataSource(XManager.getManager().getApplication(), uri);
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mPlayer.start();
        if (mMediaPlayListener != null) {
            mMediaPlayListener.onStart(mCurrentUrl);
        }
    }

    private void playFromLocal(String localFilePath) {
//        if (mPlayer == null) {
//            init();
////            return;
//        }

        try {
//            File file = new File(localFilePath);
//            FileInputStream fis = new FileInputStream(file);
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(localFilePath);
//            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
        if (mMediaPlayListener != null) {
            mMediaPlayListener.onStart(mCurrentUrl);
        }
    }
}

