package com.dfsx.ganzcms.app.act;

import java.io.File;
import java.io.IOException;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.widget.*;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.util.SupportedSizesReflect;
import com.dfsx.ganzcms.app.util.UtilHelp;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * heyang create  2016-10-17
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
    private OnRecordTimeListener mOnRecordTimeListener;// 录制完成回调接口

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件
    TextView mReordTimeTxt;
    Calendar mCalendar;
    int cameraCount = 0;
    boolean isRecordIng = false;
    Context mContext = null;
    boolean isShowTime = true;

    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    public MovieRecorderView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        // 初始化各项组件
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);
        mWidth = a.getInteger(R.styleable.MovieRecorderView_video_width, 640);// 默认320
        mHeight = a.getInteger(R.styleable.MovieRecorderView_video_height, 480);// 默认240

        isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);// 默认为10

        isShowTime = a.getBoolean(R.styleable.MovieRecorderView_isshow_time, true);// 默认240

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        //        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量
        mReordTimeTxt = (TextView) findViewById(R.id.recond_times_txt);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (!isShowTime) {
            mReordTimeTxt.setVisibility(GONE);
            ((RelativeLayout) mReordTimeTxt.getParent()).setBackgroundColor(0);
            mProgressBar.setVisibility(VISIBLE);
            mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量
        }

        a.recycle();

        mCalendar = Calendar.getInstance();
    }

    private class CustomCallBack implements Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }
    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     */
    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }

        if (cameraPosition == 1) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
            //               camera = Camera.open(cameraPosition);//打开摄像头
            //               Camera.Parameters parameters = camera.getParameters();
            //               camera.setDisplayOrientation(90);

            //               camera.setParameters(parameters);

            //            mCamera=deal(mCamera);
            //            mMediaRecorder.setOrientationHint(90);//视频旋转90度
        } else {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开摄像头
            //            Camera.Parameters parameters = mCamera.getParameters();
            //            mCamera.setDisplayOrientation(90);
            //            mCamera.setParameters(parameters);
            //            mMediaRecorder.setOrientationHint(270);//视频旋转90度
        }
        //        mCamera.unlock();


        //        try {
        //            mCamera = Camera.open();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            freeCameraResource();
        //        }
        if (mCamera == null)
            return;
        // setCameraParams();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.unlock();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        cameraCount = Camera.getNumberOfCameras();
        //        if (!isOpenCamera) {
        //            try {
        //                initCamera();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //
        if (mCamera != null) {
            freeCameraResource();
        }

        mCamera = Camera
                .open((cameraPosition + 1) % cameraCount);
        cameraPosition = (cameraPosition + 1)
                % cameraCount;
        //        Log.d(TAG,"====current camera===="+cameraPosition);

        if (mCamera == null)
            return;
        try {
            deal(mCamera);
            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.startPreview();//开始预览

        /**
         int cameraCount = 0;
         Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
         cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
         for (int i = 0; i < cameraCount; i++) {
         Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
         if (cameraPosition == 1) {
         //现在是后置，变更为前置
         if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
         mCamera.stopPreview();//停掉原来摄像头的预览
         mCamera.release();//释放资源
         mCamera = null;//取消原来摄像头
         try {
         mCamera = Camera.open(i);//打开当前选中的摄像头
         } catch (Exception e) {
         e.printStackTrace();
         freeCameraResource();
         }
         if (mCamera == null)
         return;
         try {
         deal(mCamera);
         mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
         } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
         mCamera.startPreview();//开始预览
         cameraPosition = 0;
         break;
         }
         } else {
         //现在是前置， 变更为后置
         if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
         mCamera.stopPreview();//停掉原来摄像头的预览
         mCamera.release();//释放资源
         mCamera = null;//取消原来摄像头
         mCamera = Camera.open(i);//打开当前选中的摄像头
         try {
         deal(mCamera);
         mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
         } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
         mCamera.startPreview();//开始预览
         cameraPosition = 1;
         break;
         }
         }
         **/

        //        }
    }


    //    public void switchCamera(Camera camera) {
    //        setCamera(camera);
    //        try {
    //            camera.setPreviewDisplay(mHolder);
    //        } catch (IOException exception) {
    //            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
    //        }
    //        Camera.Parameters parameters = camera.getParameters();
    //        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
    //        Camera.Size optimalSize = Util.getOptimalPreviewSize(sizes, 176, 144);
    //        // cameraCurrentlyLocked=1代表当前是前置摄像头
    //        if(cameraCurrentlyLocked==1){
    //            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
    //        }else{
    //            parameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
    //        }
    //        camera.setParameters(parameters);
    //    }

    public Camera deal(Camera camera) {
        //设置camera预览的角度，因为默认图片是倾斜90度的
        camera.setDisplayOrientation(90);

        Camera.Size pictureSize = null;
        Camera.Size previewSize = null;
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFrameRate(5);
        //设置旋转代码
        parameters.setRotation(90);
        //          parameters.setPictureFormat(PixelFormat.JPEG);

        List<Camera.Size> supportedPictureSizes
                = SupportedSizesReflect.getSupportedPictureSizes(parameters);
        List<Camera.Size> supportedPreviewSizes
                = SupportedSizesReflect.getSupportedPreviewSizes(parameters);

        if (supportedPictureSizes != null &&
                supportedPreviewSizes != null &&
                supportedPictureSizes.size() > 0 &&
                supportedPreviewSizes.size() > 0) {

            //2.x
            pictureSize = supportedPictureSizes.get(0);

            int maxSize = 1280;
            if (maxSize > 0) {
                for (Camera.Size size : supportedPictureSizes) {
                    if (maxSize >= Math.max(size.width, size.height)) {
                        pictureSize = size;
                        break;
                    }
                }
            }

            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            previewSize = getOptimalPreviewSize(
                    supportedPreviewSizes,
                    display.getWidth(),
                    display.getHeight());

            parameters.setPictureSize(pictureSize.width, pictureSize.height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }
        camera.setParameters(parameters);
        return camera;
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 设置摄像头为竖屏
     *
     */
    /*private void setCameraParams() {
        if (mCamera != null) {
            Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");
            mCamera.setParameters(params);
        }
    }*/

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createRecordDir() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "leshan/video/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File vecordDir = sampleDir;
        // 创建文件
        if (mRecordFile != null) mRecordFile = null;
        mRecordFile = new File(sampleDir.getPath(), getDate() + ".mp4");
        //            mRecordFile = File.createNewFile(getDate(), ".mp4", vecordDir); //mp4格式
        Log.i("TAG", mRecordFile.getAbsolutePath());
        String s = mRecordFile.getAbsolutePath();
    }

    /**
     * 初始化
     */
    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);// 音频格式
        //        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mMediaRecorder.setVideoEncoder(VideoEncoder.H264);
        //        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
        //        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
        mMediaRecorder.setVideoSize(1280, 720);// 设置分辨率：
        mMediaRecorder.setVideoFrameRate(30);// 这个我把它去掉了，感觉没什么用
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);// 设置帧频率，然后就清晰了
//        mMediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        //        mMediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP);// 视频录制格式
        //        mMediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);


        //        String path = getSDPath();
        //        if (path != null) {
        //            File dir = new File(path);
        //            if (!dir.exists()) {
        //                dir.mkdir();
        //            }
        //            path = dir + "/" + getDate() + ".mp4";
        //            Toast.makeText(getContext(), path, Toast.LENGTH_LONG)
        //                    .show();
        //        }
        //        mMediaRecorder.setOutputFile(path);
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecordIng = true;
    }

    public String getSDPath() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "leshan/video/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        //        File sdDir = null;
        //        boolean sdCardExist = Environment.getExternalStorageState().equals(
        //                android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
        //        if (sdCardExist) {
        //            sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
        //             Toast.makeText(this,sdDir.toString(),Toast.LENGTH_LONG).show();
        //            return sdDir.toString();
        //        } else {
        //            Toast.makeText(this, "无SD卡", Toast.LENGTH_LONG).show();
        //        }
        return sampleDir.getPath().toString();
    }

    public String getDate() {
        //        Calendar ca = Calendar.getInstance();
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        //        String date = sdf.format(ca.getTimeInMillis());
        //        return date;

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        String str = mCalendar.get(Calendar.YEAR) + "";
        str += mCalendar.get(Calendar.MONTH) + 1 + "";
        str += mCalendar.get(Calendar.DAY_OF_MONTH) + "_";
        str += mCalendar.get(Calendar.HOUR_OF_DAY) + "";
        str += mCalendar.get(Calendar.MINUTE) + "";
        str += mCalendar.get(Calendar.MILLISECOND) + "";
        return str.toString().trim();
    }

    public void onStartPreview() {
        if (!isOpenCamera)
            return;
        try {
            initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     *
     * @param
     * @param onRecordFinishListener 达到指定时间之后回调接口
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void record(final OnRecordFinishListener onRecordFinishListener,
                       final OnRecordTimeListener onRecordTimeListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        this.mOnRecordTimeListener = onRecordTimeListener;
        createRecordDir();
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            initRecord();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    if (!isShowTime)
                        mProgressBar.setProgress(mTimeCount);// 设置进度条
                    Log.e("mProgressBar=====", mTimeCount + "");
                    myHander.sendEmptyMessage(2);
                    //                    mOnRecordTimeListener.onUpdateTime(mTimeCount);
                    if (!isShowTime && mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Handler myHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                cal(mTimeCount);
            }
        }
    };

    /**
     * 停止拍摄
     */
    public void stop() {
        isRecordIng = false;
        stopRecord();
        releaseRecord();
        freeCameraResource();
        if (mRecordFile != null)
            updateMediaData(mRecordFile.getPath(), getContext());
    }

    public boolean isExist() {
        return mRecordFile == null ? true : false;
    }

    public boolean isRecordingOr() {
        return isRecordIng;
    }

    public void deleteFile() {
        Observable.just(1).
                observeOn(Schedulers.io()).
                map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        if (mRecordFile != null) {
                            String path = mRecordFile.getAbsolutePath();
                            if (TextUtils.isEmpty(path)) return false;
                            UtilHelp.deleteFile(mRecordFile);
                            updateMediaData(path, getContext());
                        }
                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        //                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean jsonObject) {

                    }
                });
    }

    public void updateMediaData(final String filename, Context context) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;// 获得当前sdk版本
        if (currentApiVersion < 19) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + filename)));
            //            ColumnData dat = new ColumnData(ColumnTYPE.LIVE_SAVE_MSG, filename);
            //            RxBus.getIntance().post(dat);
            //            if (mRecordFile != null)
            //                mRecordFile.delete();
        } else {
            MediaScannerConnection.scanFile(context, new String[]{filename},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                            //                            ColumnData dat = new ColumnData(ColumnTYPE.LIVE_SAVE_MSG, filename);
                            //                            RxBus.getIntance().post(dat);
                            //                            if (mRecordFile != null)
                            //                                mRecordFile.delete();
                        }
                    });
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        mTimeCount = 0;// 时间计数器重新赋值
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    public void restartPreview() {
        onStartPreview();
        cal(0);
        if (mRecordFile != null) mRecordFile = null;
    }

    public void cal(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        String strformat = "%02d";
        String sh = String.format(strformat, h);
        String sMiute = String.format(strformat, d);
        String sS = String.format(strformat, s);
        String str = sh + ":" + sMiute + ":" + sS;
        mReordTimeTxt.setText(str);
    }

    /**
     * 更新时间
     */
    public interface OnRecordTimeListener {
        public void onUpdateTime(int fal);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}