package com.dfsx.ganzcms.app.act;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dfsx.core.PermissionsActivity;
import com.dfsx.core.PermissionsChecker;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.fragment.FileFragment;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * heyang  create 2016-10-17
 */

public class VideoRecordAct extends BaseActivity implements View.OnClickListener, MovieRecorderView.OnRecordTimeListener {
    private MovieRecorderView mRecorderView;
    private ImageButton mShootBtn;
    private static final int UPDATE_PROCESS = 0x000007;
    private boolean isFinish = true;
    protected SystemBarTintManager systemBarTintManager;
    protected FrameLayout activityContainer;
    private static final int REQUEST_CODE = 0;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // Ȩ�޼����
    Button mApplyBtn;
    ImageButton mSwitchBtn;
    ImageButton mClosedBtn;
    ImageButton mDeleteFileBtn;
    TextView mReordTimeTxt;
    Calendar mCalendar;
    Button mLocalUpbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        systemBarTintManager = Util.applyKitKatTranslucency(this,
//                getStatusBarColor());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main_app_rerocd);
        mReordTimeTxt = (TextView) findViewById(R.id.recond_time_txt);
        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (ImageButton) findViewById(R.id.shoot_button);
        mApplyBtn = (Button) findViewById(R.id.video_apply_button);
        mApplyBtn.setOnClickListener(this);
        mSwitchBtn = (ImageButton) findViewById(R.id.switch_camera_button);
        mSwitchBtn.setOnClickListener(this);
        mLocalUpbtn = (Button) findViewById(R.id.localtion_fileup_btn);
        mLocalUpbtn.setOnClickListener(this);
        mDeleteFileBtn = (ImageButton) findViewById(R.id.delete_button);
        mDeleteFileBtn.setOnClickListener(this);
        mClosedBtn = (ImageButton) findViewById(R.id.closed_button);
        mClosedBtn.setOnClickListener(this);
        mShootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFinish) {
                    isFinish = false;
                    mShootBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_record_stop));
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
                                             @Override
                                             public void onRecordFinish() {
//                            handler.sendEmptyMessage(1);
                                             }
                                         },
                            new MovieRecorderView.OnRecordTimeListener() {
                                @Override
                                public void onUpdateTime(int fal) {
                                    Message msg = handler.obtainMessage(UPDATE_PROCESS);
                                    msg.what = fal;
                                    handler.sendMessage(msg);
                                }
                            }
                    );
                } else {
//                    if (mRecorderView.getTimeCount() > 1)
//                        handler.sendEmptyMessage(1);
//                    else
                    {
//                        if (mRecorderView.getmRecordFile() != null)
//                            mRecorderView.getmRecordFile().delete();
                        stop();
//                        Toast.makeText(VideoRecordAct.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /**
         mShootBtn.setOnTouchListener(new OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

        @Override public void onRecordFinish() {
        handler.sendEmptyMessage(1);
        }
        });
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
        if (mRecorderView.getTimeCount() > 1)
        handler.sendEmptyMessage(1);
        else {
        if (mRecorderView.getmRecordFile() != null)
        mRecorderView.getmRecordFile().delete();
        mRecorderView.stop();
        Toast.makeText(VideoRecordAct.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
        }
        }
        return true;
        }
        });
         **/
        mCalendar = Calendar.getInstance();
        mPermissionsChecker = new PermissionsChecker(this);
    }

    public void stop() {
        isFinish = true;
        mShootBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_record_start));
        mSwitchBtn.setVisibility(View.GONE);
        mApplyBtn.setVisibility(View.VISIBLE);
        mRecorderView.stop();
        mReordTimeTxt.setText("00:00:00");
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

    public int getStatusBarColor() {
        return this.getResources().getColor(Color.BLACK);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // �ܾ�ʱ, �ر�ҳ��, ȱ����ҪȨ��, �޷�����
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
        if (resultCode != RESULT_OK) {
            return;
        } else {
            Cursor cursor = null;
            switch (requestCode) {
                case 2: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                        Intent intent = new Intent();
                        intent.putExtra("paths", bitmapList);
                        DefaultFragmentActivity.start(VideoRecordAct.this, FileFragment.class.getName(), intent);
                    }
//                    if (!bitmapList.isEmpty()) {
//                        for (int i = 0; i < bitmapList.size(); i++) {
//                            MediaModel model = bitmapList.get(i);
//                            if (model != null) {
//                                model.type = 1;
//                                adapter.addTail(model);
//                            }
//
//                        }
//                    }
                }
                break;
            }
            setResult(1, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void setContentView(View view) {
//        activityContainer = new FrameLayout(this);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        activityContainer.setLayoutParams(params);
//        activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
//        activityContainer.addView(view);
//        super.setContentView(activityContainer);
//    }

    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
//        if (Util.isMMc()) {
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
//        }
        if (!mRecorderView.isRecordingOr() && !mRecorderView.isExist()) {
            mRecorderView.restartPreview();
            if (mApplyBtn != null) mApplyBtn.setVisibility(View.GONE);
            //重新预览
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_PROCESS) {
                int favl = (int) msg.obj;
                cal(favl);
            }
//            if(msg.what==1){
//                finishActivity();
//            }
        }
    };

    private void finishActivity() {
        if (!isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
//            Intent intent = new Intent();
//            Log.d("TAG",mRecorderView.getmRecordFile().getAbsolutePath());
//            intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
//            setResult(RESULT_OK,intent);
        }
        isFinish = true;
//        finish();
    }


    @Override
    public void onClick(View view) {
        if (view == mApplyBtn) {
            if (view == mLocalUpbtn) stop();
            if (mRecorderView == null) return;
            String filePath = mRecorderView.getmRecordFile().getPath();
            if (filePath != null && !TextUtils.isEmpty(filePath)) {
                MediaModel model = new MediaModel();
                model.type = 1;
                model.url = filePath;
                ArrayList<MediaModel> arr = new ArrayList<MediaModel>();
                arr.add(model);
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("paths", arr);
                DefaultFragmentActivity.start(VideoRecordAct.this, FileFragment.class.getName(), intent);
            }
        } else if (view == mClosedBtn) {
            if (mRecorderView.isExist()) finish();
            if (mRecorderView.isRecordingOr()) {
                mRecorderView.stop();
                finish();
            } else if (!mRecorderView.isRecordingOr() && !mRecorderView.isExist()) {
                mRecorderView.restartPreview();
                if (mApplyBtn != null) mApplyBtn.setVisibility(View.GONE);
                //重新预览
            }
        } else if (view == mDeleteFileBtn) {
            mRecorderView.deleteFile();
        } else if (view == mSwitchBtn) {
            mRecorderView.switchCamera();
        } else if (view == mLocalUpbtn) {
//            Intent intent = new Intent();
//            Bundle data = new Bundle();
//            data.putBoolean(VideoFragmentActivity.KEY_SINGLE_MODE, true);
//            intent.putExtras(data);
//            intent.setClass(this, VideoFragmentActivity.class);
//            startActivityForResult(intent, 2);
//            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            gotoSelectVideo();
        }
    }


    public void gotoSelectVideo() {
        new TedPermission(this).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(VideoRecordAct.this, VideoFragmentActivity.class);
                intent.putExtra("KEY_SINGLE_MODE", true);
//                intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, 9);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(VideoRecordAct.this, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    @Override
    public void onUpdateTime(int fal) {
        Log.i("favc========", fal + "");
        Message msg = handler.obtainMessage(UPDATE_PROCESS);
        msg.what = fal;
        handler.sendMessage(msg);
//        countTime(fal);
    }

    /**
     * 录制完成回调
     *
     * @author liuyinjun
     * @date 2015-2-9
     */
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);

        public void OnShootFailure();
    }

  /*  //  private Button btnVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       *//* btnVideo = (Button) findViewById(R.id.btn_video);

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                String url = createFilePath("QDbzxt_video");
                File file = new File(url,"mov_"+getCurrentDate()+".mp4");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 设置存储路径
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // 设置保存质量
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); // 设置限制时长
                //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,100000L); // 设置限制大小
                startActivityForResult(intent, 0x123);

            }
        });*//*
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x123:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_LONG).show();
                    // 获取第一帧图片

                }
                break;
        }
    }

    *//**
     * 创建文件夹路径
     * @param folderName　文件夹名称
     * @return 文件夹路径
     *//*
    public static String createFilePath(String folderName) {
        File file = new File(android.os.Environment.getExternalStorageDirectory()+"/"+folderName);
        file.mkdir();
        String path = file.getPath();
        return path;
    }

    *//**
     * 获取当前时间
     * @return 日期的字符串信息
     *//*
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String strDate = dateFormat.format(new Date());
        return strDate;
    }*/

}
