package com.dfsx.ganzcms.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressRequestListener;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.file.processWnd.ProcessDialog;
import com.dfsx.core.network.ProgessCallbacface;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.GridViewAdapter;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.util.LogUtils;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.ImageFragmentActivity;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import rx.Observer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by heyang on 2016/9/18.
 */
public class FileFragment extends Fragment implements View.OnClickListener, FragmentBackHandler, ProgessCallbacface, EditChangedLister.EditeTextStatuimpl {
    public static final String TAG = "FileFragment";
    public static final int RESULT_OK = -1;
    private static final int SELECT_VIDEO = 5;
    private static final int SELECT_IMAG = 2;
    private static final int SELECT_AUDIO = 6;
    private static final int UPDATE_PROGRESS = 0x000034;
    GridView mGridView = null;
    GridViewAdapter adapter;
    private boolean mIsLongPres = false;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private static Uri fileUri;
    ImageButton backBtn;
    ArrayList<MediaModel> ulsAttr;
    EditText mEdtTitle;
    EditText mEditContent;
    ImageButton mBtnPubfile;
    ContentCmsApi mContentCmsApi = null;
    ProcessDialog mProcessDialog = null;

    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == UPDATE_PROGRESS) {
                int values = (int) message.obj;
                if (values != -1 && mProcessDialog != null)
                    mProcessDialog.updateValues(values);
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.video_file_act, null);
        if (getArguments() != null) {
            ulsAttr = getArguments().getParcelableArrayList("paths");
        }
        mBtnPubfile = (ImageButton) rootView.findViewById(R.id.video_publish_btn);
        mBtnPubfile.setOnClickListener(this);
        mEdtTitle = (EditText) rootView.findViewById(R.id.video_title_edt);
        mEdtTitle.addTextChangedListener(new EditChangedLister(this));
        mEditContent = (EditText) rootView.findViewById(R.id.video_content_edt);
        mEditContent.addTextChangedListener(new EditChangedLister(this));
        backBtn = (ImageButton) rootView.findViewById(R.id.btn_cideo_retun);
        backBtn.setOnClickListener(this);
        mGridView = (GridView) rootView.findViewById(R.id.fileup_video_gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridViewAdapter(getActivity());
        adapter.setMaxNumber(1);
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
                            File file = new File(item.url);
                            intents.setDataAndType(Uri.fromFile(file), "image/*");
                            startActivity(intents);
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
        mContentCmsApi = new ContentCmsApi(getActivity());
        mProcessDialog = new ProcessDialog(getActivity());
        mProcessDialog.set_onCloseLister(new ProcessDialog.OnCancelBtnLister() {
            @Override
            public void onClosed() {
                App.getInstance().setUpFileFlag(false);
                closeProcessDialog();
            }
        });
        init();
        if (ulsAttr != null && !ulsAttr.isEmpty()) {
            for (MediaModel model : ulsAttr) {
                adapter.addTail(model);
            }
        }
        return rootView;
    }

    public void closeProcessDialog() {
        if (mProcessDialog != null && mProcessDialog.isShowActivty())
            mProcessDialog.dismissDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void init() {
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
//        bt1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                takePhotos();
//                pop.dismiss();
//                ll_popup.clearAnimation();
//            }
//        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageFragmentActivity.class);
                startActivityForResult(intent, SELECT_IMAG);
                getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
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
                                String img_path = model.url.toString();
                                img_path = UtilHelp.getimage(img_path);
                                adapter.addTail(model);
                            }
                        }
                    }
                }
                break;
                /**
                 case CAMERA_ACTIVITY: {
                 try {
                 getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                 String sp = data.getData().toString();
                 String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                 //                        if (adapter.getSize() < 1) {
                 MediaEntry item = new MediaEntry();
                 item.type = 1;
                 //                            deleteDefaultFile(Uri.parse(data.getData().toString()),item,UtilHelp.PUBLIC_PATH,tmpFile);
                 //                            if(sp.startsWith("file://"));
                 //                            {
                 //                                sp=sp.substring(7,sp.length());
                 //                            }
                 cursor = getActivity().getContentResolver().query(Uri.parse(fileUriString), null,
                 null, null, null);
                 if (cursor != null && cursor.moveToNext()) {
                 int columnIndex = cursor
                 .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                 String fileName = cursor.getString(columnIndex);
                 item.path = fileName;
                 //获取缩略图id
                 int id = cursor.getInt(cursor
                 .getColumnIndex(MediaStore.Video.VideoColumns._ID));
                 int lafIndex = cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE);
                 double lafs = Double.parseDouble(cursor
                 .getString(lafIndex));
                 int logfIndex = cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE);
                 double logfs = Double.parseDouble(cursor
                 .getString(logfIndex));
                 int nId = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                 String namse = cursor
                 .getString(nId);
                 item.latitude = lafs;
                 item.longitude = logfs;
                 }
                 item.path = fileUriString;
                 adapter.addTail(item);
                 //                        } else {
                 //                            Toast.makeText(getActivity(), "上传个数已满!!!", Toast.LENGTH_LONG).show();
                 //                        }
                 } catch (Exception io_e) {
                 io_e.printStackTrace();
                 }
                 }
                 break;   **/
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

    public boolean isUserNull() {
        boolean isLogo = false;
        if (App.getInstance().getUser() == null) {
            isLogo = true;
            goToLogin();
        }
        return isLogo;
    }

    private void goToLogin() {
        if (getActivity() != null) {
            AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("未登录，是否现在登录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
        if (view == backBtn) {
            getActivity().finish();
        } else if (view == mBtnPubfile) {
            if (isUserNull()) return;
            LogUtils.e(TAG, "点击上传开始 ======= start  count=" + adapter.getCount());
            String title = mEdtTitle.getText().toString();
            String conten = mEditContent.getText().toString();
            mProcessDialog.showDialog(view);
            ArrayList<MediaModel> dlist = adapter.getAllItems();
            App.getInstance().setUpFileFlag(true);
            mContentCmsApi.upVideoDiscluare(title, conten, dlist, new DataUIProgressRequestListener(), new Observer<Long>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(Long aLong) {
                    closeProcessDialog();
                    if (aLong == 0) return;
                    String msg = "上传视频成功";
                    if (aLong == -1) {
                        msg = "上传视频失败";
                    } else {
                        mEdtTitle.setText("");
                        mEditContent.setText("");
                        adapter.removeAlls();
                    }
                    LogUtils.e(TAG, "点击上传开始 完成==  " + msg);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mBtnPubfile.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mBtnPubfile.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

    class DataUIProgressRequestListener extends UIProgressRequestListener {

        private int index;
        private int allCount;

        private int currentPercent;

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
            float p = (float) bytesWrite / contentLength * 100;
//            currentPercent = (int) (index * 100 / allCount + p / allCount);
//            boolean isDone = (index == (allCount - 1)) && done;
//            if (uploadPercentListener != null) {
//                uploadPercentListener.onUploadPercent(currentPercent, isDone);
//            }
            Message msg = myHander.obtainMessage(UPDATE_PROGRESS);
            msg.obj = (int) contentLength;
            myHander.sendMessage(msg);
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }
    }

    @Override
    public boolean MyObtainProgressValues(int values) {
        Log.e(TAG, "values====" + values);
        Message msg = myHander.obtainMessage(UPDATE_PROGRESS);
        msg.obj = values;
        myHander.sendMessage(msg);
        return false;
    }

    @Override
    public boolean onBackPressed() {
        // 当确认没有子Fragmnt时可以直接return false
        if (mProcessDialog != null) {
            if (mProcessDialog.isShowActivty()) {
                mProcessDialog.dismissDialog();
                mProcessDialog = null;
                App.getInstance().setUpFileFlag(false);
                return true;
            }
        }
        return BackHandlerHelper.handleBackPress(this);
    }


}
