package com.dfsx.ganzcms.app.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.processWnd.ProcessDialog;
import com.dfsx.core.network.ProgessCallbacface;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.adapter.GridViewAdapter;
import com.dfsx.ganzcms.app.business.PictureManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.R;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import rx.Observer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by heyang on 2017/6/21
 * 提问
 */
public class ComitQustionFragment extends Fragment implements View.OnClickListener, DefaultFragmentActivity.ViewClickLister, EditChangedLister.EditeTextStatuimpl {
    public static final int RESULT_OK = -1;
    private static final int SELECT_VIDEO = 5;
    private static final int SELECT_IMAG = 2;
    private static final int SELECT_AUDIO = 6;
    private static final int CAMERA_ACTIVITY = 7;
    GridView mGridView = null;
    GridViewAdapter adapter;
    private boolean mIsLongPres = false;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private static Uri fileUri;
    ImageButton backBtn;
    TextView mAddPictureBtn;
    ImageButton mPublishBtn;
    ProcessDialog mProcessDialog;
    private int mColumnId = -1;
    EditText mTitleEdt;
    EditText mContenEdt;
    TopicalApi mTopicalApi = null;
    private boolean isCopmlate = true;
    View rootView;
    ProgressDialog progressDialog;
    MyquswerTabFragment tabFrag ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.commit_question_activity, null);
        if (getArguments() != null) {
            mColumnId = getArguments().getInt("id", -1);
        }
        mTitleEdt = (EditText) rootView.findViewById(R.id.commit_question_title_edt);
        mContenEdt = (EditText) rootView.findViewById(R.id.commit_question_content_edt);
        mContenEdt.addTextChangedListener(new EditChangedLister(this));
        mAddPictureBtn = (TextView) rootView.findViewById(R.id.commnity_add_pic_btn);
        mAddPictureBtn.setOnClickListener(this);
//        mPublishBtn = (ImageButton) rootView.findViewById(R.id.commonity_publish_btn);
//        mPublishBtn.setOnClickListener(this);
        mGridView = (GridView) rootView.findViewById(R.id.fileup_imgs_gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridViewAdapter(getActivity());
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                if (mIsLongPres) return;
                if (arg2 == adapter.getSize()) {
//                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
                    pop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent();
                    int nCount = adapter.getSize();
                    if (nCount > 0) {
                        String object = "";
                        MediaModel item = (MediaModel) adapter.getItem(arg2);
                        Intent intents = new Intent(Intent.ACTION_VIEW);
                        if (item.type == 0) {
                            OpenImageUtils.openImage(getContext(), item.url, arg2);
                        } else if (item.type == 1) {
                            File file = new File(item.url);
                            intents.setDataAndType(Uri.fromFile(file), "video/*");
                            startActivity(intents);
                        } else {
                            return;
                        }
                    }
                }
            }
        });
        mTopicalApi = new TopicalApi(getActivity());
        mProcessDialog = new ProcessDialog(getActivity());
        init();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在提交");
        return rootView;
    }

    @Override
    public void AreaClick(MotionEvent event) {
        if (isShouldHideInput(mContenEdt, event)) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mContenEdt.getWindowToken(), 0);
            }
        }
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void gotoSelectImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
//                intent.putExtra("KEY_SINGLE_MODE", true);
                intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, 9);
                startActivityForResult(intent, SELECT_IMAG);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }


    public void init() {
        mContenEdt.setFocusable(true);
        mContenEdt.requestFocus();
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager) mContenEdt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mContenEdt, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300);

        pop = new PopupWindow(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        Button bt4 = (Button) view.findViewById(R.id.item_popupwindows_video);
        Button bt5 = (Button) view.findViewById(R.id.item_popupwindows_audio);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideInputSoft();
                gotoTakeImage();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideInputSoft();
                gotoSelectImage();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputSoft();
                Intent intent = new Intent();
                intent.setClass(getActivity(), VideoFragmentActivity.class);
                startActivityForResult(intent, SELECT_VIDEO);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

//        bt5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), ChooseAudios.class);
//                startActivityForResult(intent, SELECT_AUDIO);
//                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//                pop.dismiss();
//                ll_popup.clearAnimation();
//            }
//        });
    }

    public void hideInputSoft() {
        InputMethodManager imm = (InputMethodManager) mContenEdt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(mContenEdt.getWindowToken(), 0);
        }
    }

    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                    hideInputSoft();
                    return false; //已处理
                }
            }
            return false;
        }
    };

    public void gotoTakeImage() {
        new TedPermission(getActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                takePhotos();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
            }
        }).setDeniedMessage(getActivity().getResources().getString(R.string.denied_message)).
                setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }

    public void takePhotos() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_IMAGE); // create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(UtilHelp.PUBLIC_PATH);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MediaChooserConstants.SELECTED_MEDIA_COUNT = 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            Cursor cursor = null;
            switch (requestCode) {
                /**
                 case 1:
                 getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                 String fileUrString = fileUri.toString().replaceFirst("file:///", "/").trim();
                 //                    if (adapter.getSize() < 1) {
                 cursor = getActivity().getContentResolver().query(Uri.parse(fileUrString), null,
                 null, null, null);
                 double laf = 0.0f;
                 double logf = 0.0f;
                 if (cursor != null) {
                 if (cursor.moveToNext()) {
                 int lafIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
                 laf = Double.parseDouble(cursor
                 .getString(lafIndex));
                 int logfIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
                 logf = Double.parseDouble(cursor
                 .getString(logfIndex));
                 }
                 }
                 adapter.addTail(fileUrString, fileUrString, 0, laf, logf);
                 break;   **/
                case SELECT_IMAG:    //选
                {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    if (!bitmapList.isEmpty()) {
                        for (int i = 0; i < bitmapList.size(); i++) {
                            MediaModel model = bitmapList.get(i);
                            if (model != null) {
                                model.type = 0;
//                                String img_path = model.url.toString();
//                                img_path = UtilHelp.getimage(img_path);
                                adapter.addTail(model);
                            }
                        }
                    }
                }
                break;
                case CAMERA_ACTIVITY: {
                    try {
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        adapter.addTail(fileUriString, 0, 0, 0);
                    } catch (Exception io_e) {
                        io_e.printStackTrace();
                    }
                }
                break;
                case SELECT_VIDEO: {
                    Bundle be = data.getExtras();
                    ArrayList<MediaModel> bitmapList = null;
                    if (null != be) {
                        bitmapList = (ArrayList<MediaModel>) be.get("list");
                    }
                    if (!bitmapList.isEmpty()) {
                        for (int i = 0; i < bitmapList.size(); i++) {
                            MediaModel model = bitmapList.get(i);
                            if (model != null) {
                                model.type = 1;
                                adapter.addTail(model);
                            }

                        }
                    }
                }
                break;
                case SELECT_AUDIO: {
                    if (data != null) {
                        ArrayList<String> slist = data.getStringArrayListExtra("list");
                        if (slist != null) {
                            if (!slist.isEmpty()) {
                                for (int i = 0; i < slist.size(); i++) {
                                    MediaModel entry = new MediaModel(slist.get(i).toString(), 2);
                                    adapter.addTail(entry);
                                }
                            }
                        } else {
                            String path = data.getData().getPath();
                            MediaModel entry = new MediaModel(path.toString(), 2);
                            adapter.addTail(entry);
                        }
                    }
                }
                break;
            }

            getActivity().setResult(1, data);
//            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
//            mPublishBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
//            mPublishBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
        if (view == backBtn) {
            hideInputSoft();
            getActivity().finish();
        } else if (view == mAddPictureBtn) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ImageFragmentActivity.class);
            intent.putExtra(ImageFragmentActivity.KEY_MAX_MODE, 9);
            startActivityForResult(intent, SELECT_IMAG);
            getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
        } else if (view == mPublishBtn) {
            if (isUserNull()) return;
            if (TextUtils.equals(mContenEdt.getText(), "")) {
                Toast.makeText(getActivity(), "发表内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            hideInputSoft();
//            mProcessDialog.showDialog(view);
            try {
                if (!isCopmlate) return;
                progressDialog.show();
                createTopical();
            } catch (ApiException e) {
                isCopmlate = true;
                e.printStackTrace();
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToLogin() {
        if (getActivity() != null) {
            AlertDialog adig = new AlertDialog.Builder(getActivity
                    ()).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            }).create();
            adig.show();
        }
    }

    public boolean isUserNull() {
        boolean isLogo = false;
        if (App.getInstance().getUser() == null) {
            isLogo = true;
            goToLogin();
        }
        return isLogo;
    }

    public void createTopical() throws ApiException {
        isCopmlate = false;
        String title = mTitleEdt.getText().toString().trim();
        String content = mContenEdt.getText().toString().trim();
        ArrayList<MediaModel> arr = adapter.getAllItems();
        if (arr != null && arr.size() > 0) {
            PictureManager.getInstance().compressionImage(arr);
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(PictureManager.getInstance().saveDirTemp)));
        }
        ArrayList<MediaModel> list = PictureManager.getInstance().getmDllist();
        mTopicalApi.publisTopic(mColumnId, content, list,null, new ProgessCallbacface() {
            @Override
            public boolean MyObtainProgressValues(int values) {

                return false;
            }
        }, new Observer<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                isCopmlate = true;
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                PictureManager.getInstance().clearCache();
            }

            @Override
            public void onNext(Long aLong) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                isCopmlate = true;
                if (aLong != -1) {
                    RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                    Toast.makeText(getActivity(), "发布主题成功", Toast.LENGTH_LONG).show();
                    PictureManager.getInstance().clearCache();
                    getActivity().finish();
                }
            }
        });
    }
}
