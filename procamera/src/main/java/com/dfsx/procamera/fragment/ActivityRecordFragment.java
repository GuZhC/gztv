package com.dfsx.procamera.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dfsx.core.common.Util.Util;
import com.dfsx.procamera.R;
import com.dfsx.procamera.busniness.IActivtiySelectItemiter;
import com.dfsx.procamera.view.camera.JCameraView;
import com.dfsx.procamera.view.camera.listener.ClickListener;
import com.dfsx.procamera.view.camera.listener.JCameraListener;

import java.io.File;

/*
 *  Created  by heyang  2018-6-22
 */

public class ActivityRecordFragment extends Fragment {
    private View rootView;
    private JCameraView jCameraView;
    private IActivtiySelectItemiter selectItemiter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_record_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
        }
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
        initView(view);
    }

    public void setSelectItemiter(IActivtiySelectItemiter selectItemiter) {
        this.selectItemiter = selectItemiter;
    }


    public void initView(View view) {
        jCameraView = (JCameraView) view.findViewById(R.id.jcameraview);

        //设置视频保存路径
//        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setSaveVideoPath(Util.PUBLIC_VIDEO_PATH);

        //设置只能录像或只能拍照或两种都可以（默认两种都可以）
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);

        //设置视频质量
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_HIGH);


        //JCameraView 监听
//        jCameraView.setErrorLisenter(new ErrorLisenter() {
//            @Override
//            public void onError() {
//                //打开 Camera 失败回调
//                Log.i("CJT", "open camera error");
//            }
//
//            @Override
//            public void AudioPermissionError() {
//                //没有录取权限回调
//                Log.i("CJT", "AudioPermissionError");
//            }
//        });

//        jCameraView.setJCameraLisenter(new JCameraLisenter() {
//            @Override
//            public void captureSuccess(Bitmap bitmap) {
//                //获取图片 bitmap
//                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
//            }
//
//            @Override
//            public void recordSuccess(String url, Bitmap firstFrame) {
//                //获取视频路径
//                Log.i("CJT", "url = " + url);
//            }
//            //@Override
//            //public void quit() {
//            //    (1.1.9+后用左边按钮的点击事件替换)
//            //}
//        });

        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {

            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                String path = url;
                if (!(TextUtils.isEmpty(path))) {
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
                    if (selectItemiter != null)
                        selectItemiter.OnComplete(path);
                }
            }
        });

        //左边按钮点击事件
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                if (selectItemiter != null)
                    selectItemiter.onCancel();
            }
        });

        //右边按钮点击事件
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(getActivity(), "Right", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

}
