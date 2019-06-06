package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.ganzcms.app.act.MovieRecorderView;
import com.dfsx.ganzcms.app.R;
import com.dfsx.selectedmedia.MediaModel;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by heyang on 2016/9/18.
 */
public class CommonityRecordFragment extends Fragment implements View.OnClickListener, MovieRecorderView.OnRecordTimeListener {
    public static final String TAG = "CommonityRecordFragment";
    private MovieRecorderView mRecorderView;
    private ImageView mShootBtn;
    private static final int UPDATE_PROCESS = 0x000007;
    private static final int STOP_PROCESS = 0x000009;
    private boolean isFinish = true;
    protected SystemBarTintManager systemBarTintManager;
    protected FrameLayout activityContainer;
    private static final int REQUEST_CODE = 0;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView mApplyBtn;
    ImageView mSwitchBtn;
    ImageView mClosedBtn;
    ImageView mDeleteFileBtn;
    TextView mReordTimeTxt;
    Calendar mCalendar;
    SeekBar mProgrssBar;
    private long mClolumType;
    /**
     * 通过StartActivityForResult的方式返回数据
     */
    private boolean isSetResult;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(getActivity()==null) return;
            if (msg.what == UPDATE_PROCESS) {
                int favl = (int) msg.obj;
                //                cal(favl);
                mProgrssBar.setProgress(favl);
            } else if (msg.what == STOP_PROCESS) {
                Toast.makeText(getContext(), "最大拍摄视频为10秒!", Toast.LENGTH_SHORT).show();
                stop();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.commonity_video_pub_record, null);
        if (getArguments() != null) {
            mClolumType = getArguments().getLong("id", -1);
            isSetResult = getArguments().getBoolean("is_set_result", false);
        }
        initViews(rootView);
        return rootView;
    }

    public void initViews(View view) {
        mProgrssBar = (SeekBar) view.findViewById(R.id.record_seek_bar);
        mProgrssBar.setProgress(11);
        mReordTimeTxt = (TextView) view.findViewById(R.id.recond_time_txt);
        mRecorderView = (MovieRecorderView) view.findViewById(R.id.movieRecorderView);
        mShootBtn = (ImageView) view.findViewById(R.id.shoot_button);
        mApplyBtn = (ImageView) view.findViewById(R.id.video_apply_button);
        mApplyBtn.setOnClickListener(this);
        mSwitchBtn = (ImageView) view.findViewById(R.id.switch_camera_button);
        mSwitchBtn.setOnClickListener(this);
        mDeleteFileBtn = (ImageView) view.findViewById(R.id.delete_button);
        mDeleteFileBtn.setOnClickListener(this);
        //        mClosedBtn = (ImageView) view.findViewById(R.id.closed_button);
        //        mClosedBtn.setOnClickListener(this);
        mShootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRecordVideo(new CallBackInter() {
                    @Override
                    public void startIner() {
                        start();
                    }
                });
            }
        });
        mCalendar = Calendar.getInstance();
        gotoRecordVideo(new CallBackInter() {
            @Override
            public void startIner() {
                mRecorderView.restartPreview();
            }
        });
    }

    public void start() {
        if (isFinish) {
            isFinish = false;
            //            mShootBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_record_stop));
            mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
                                     @Override
                                     public void onRecordFinish() {
                                         if (!isFinish)
                                             handler.sendEmptyMessage(STOP_PROCESS);
                                     }
                                 },
                    new MovieRecorderView.OnRecordTimeListener() {
                        @Override
                        public void onUpdateTime(int fal) {
                            //                            Message msg = handler.obtainMessage(UPDATE_PROCESS);
                            //                            msg.what = fal;
                            //                            handler.sendMessage(msg);
                        }
                    }
            );
        } else {
            stop();
        }
    }

    public void stop() {
        isFinish = true;
        showButtonUI(false);
        //        mShootBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_record_start));
        mRecorderView.stop();
        //        mReordTimeTxt.setText("00:00:00");
    }

    @Override
    public void onResume() {
        super.onResume();
        //       isFinish = true;
        //        if (!mRecorderView.isRecordingOr() && !mRecorderView.isExist()) {
        //            isFinish = true;
        //            mRecorderView.restartPreview();
        //            if (mApplyBtn != null) mApplyBtn.setVisibility(View.GONE);
        //            //重新预览
        //        }
    }

    @Override
    public void onClick(View view) {
        if (view == mApplyBtn) {
            if (mRecorderView == null) return;
            String filePath = mRecorderView.getmRecordFile().getPath();
            if (filePath != null && !TextUtils.isEmpty(filePath)) {
                //                MediaModel model = new MediaModel();
                //                model.type = 1;
                //                model.url = filePath;
                //                ArrayList<MediaModel> arr = new ArrayList<MediaModel>();
                //                arr.add(model);
                //                Intent intent = new Intent();
                //                intent.putParcelableArrayListExtra("paths", arr);
                //                DefaultFragmentActivity.start(getActivity(), FileFragment.class.getName(), intent);

                MediaModel model = new MediaModel();
                model.type = 1;
                model.url = filePath;
                Intent intent = new Intent();
                intent.putExtra("id", mClolumType);
                intent.putExtra("path", filePath);
                intent.putExtra("type", 1);
                if (isSetResult) {
                    getActivity().setResult(Activity.RESULT_OK, intent);
                } else {
                    DefaultFragmentActivity.start(getActivity(), CommunityPubFileFragment.class.getName(), intent);
                }
                getActivity().finish();
            }
        } else if (view == mClosedBtn) {
            if (mRecorderView.isExist()) getActivity().finish();
            if (mRecorderView.isRecordingOr()) {
                mRecorderView.stop();
                getActivity().finish();
            } else if (!mRecorderView.isRecordingOr() && !mRecorderView.isExist()) {
                mRecorderView.restartPreview();
                if (mApplyBtn != null) mApplyBtn.setVisibility(View.GONE);
                //重新预览
            }
        } else if (view == mDeleteFileBtn) {
            mRecorderView.deleteFile();
            mRecorderView.restartPreview();
            isFinish = true;
            showButtonUI(true);
        } else if (view == mSwitchBtn) {
            mRecorderView.switchCamera();
        }
        //        else if (view == mLocalUpbtn) {
        //            Intent intent = new Intent();
        //            Bundle data = new Bundle();
        //            data.putBoolean(VideoFragmentActivity.KEY_SINGLE_MODE, true);
        //            intent.putExtras(data);
        //            intent.setClass(this, VideoFragmentActivity.class);
        //            startActivityForResult(intent, 2);
        //            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
        //            gotoSelectVideo();
        //        }
    }

    public void showButtonUI(boolean iStart) {
        if (iStart) {
            mShootBtn.setVisibility(View.VISIBLE);
            mSwitchBtn.setVisibility(View.VISIBLE);
            mDeleteFileBtn.setVisibility(View.GONE);
            mApplyBtn.setVisibility(View.GONE);
        } else {
            mShootBtn.setVisibility(View.INVISIBLE);
            mSwitchBtn.setVisibility(View.INVISIBLE);
            mDeleteFileBtn.setVisibility(View.VISIBLE);
            mApplyBtn.setVisibility(View.VISIBLE);
        }
    }


    public interface CallBackInter {
        public void startIner();
    }

    public void gotoRecordVideo(final CallBackInter callback) {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (callback != null)
                    callback.startIner();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    @Override
    public void onUpdateTime(int fal) {
        Log.i("favc========", fal + "");
        //        Message msg = handler.obtainMessage(UPDATE_PROCESS);
        //        msg.what = fal;
        //        handler.sendMessage(msg);
    }
}
