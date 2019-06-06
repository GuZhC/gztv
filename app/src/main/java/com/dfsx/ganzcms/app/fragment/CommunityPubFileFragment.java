package com.dfsx.ganzcms.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.file.processWnd.ProcessDialog;
import com.dfsx.core.file.upload.PublishData;
import com.dfsx.core.file.upload.UploadFileData;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.ProgessCallbacface;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ComunityFullVideoActivity;
import com.dfsx.ganzcms.app.adapter.GridViewAdapter;
import com.dfsx.ganzcms.app.business.*;
import com.dfsx.ganzcms.app.model.IUploadFile;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.ganzcms.app.model.UploadAttment;
import com.dfsx.ganzcms.app.view.CustomLabelLayout;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.selectedmedia.MediaChooserConstants;
import com.dfsx.selectedmedia.MediaModel;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
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

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by heyang on 2016/10/17.
 */
public class CommunityPubFileFragment extends BaseLocaFileFragment implements View.OnClickListener, FragmentBackHandler, PictureManager.OnDataComplateInitLister, DefaultFragmentActivity.ViewClickLister, EditChangedLister.EditeTextStatuimpl {
    GridView mGridView = null;
    GridViewAdapter adapter;
    private boolean mIsLongPres = false;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    ImageButton backBtn;
    ImageButton mPublishBtn;
    private long mColumnId = -1;
    private int type = 0;  // 0: 图片    1：视频
    EditText mTitleEdt;
    EditText mContenEdt;
    TopicalApi mTopicalApi = null;
    private boolean isCopmlate = true;
    View rootView;
    ProgressDialog progressDialog;
    TextView mTopTitle;
    String path;
    ImageView mAddTabLabel;
    CustomLabelLayout mTabGrouplayout;
    List<String> mSelectLabels;
    List<String> mTotakLabels = null;
    Subscription mSubscription = null;
    Subscription mUpfileSubscription = null;
    ProcessDialog mProcessDialog = null;
    private IsLoginCheck mloginCheck;

    private ArrayList<IUploadFile> imageUploadFileList;
    private ArrayList<IUploadFile> videoUploadFileList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.community_file_activity, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mColumnId = getArguments().getLong("id", -1);
            type = getArguments().getInt("type", 0);
            path = getArguments().getString("path");
        }
        mAddTabLabel = (ImageView) rootView.findViewById(R.id.add_comunt_label_image);
        mAddTabLabel.setOnClickListener(this);
        mTabGrouplayout = (CustomLabelLayout) rootView.findViewById(R.id.community_label_view);
        mTopTitle = (TextView) rootView.findViewById(R.id.community_pub_top_title);
        mTitleEdt = (EditText) rootView.findViewById(R.id.commit_title_edt);
        mContenEdt = (EditText) rootView.findViewById(R.id.commit_content_edt);
        mContenEdt.addTextChangedListener(new EditChangedLister(this));
        backBtn = (ImageButton) rootView.findViewById(R.id.btn_retun);
        backBtn.setOnClickListener(this);
        mPublishBtn = (ImageButton) rootView.findViewById(R.id.commonity_publish_btn);
        mPublishBtn.setOnClickListener(this);
        mGridView = (GridView) rootView.findViewById(R.id.fileup_imgs_gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridViewAdapter(getActivity());
        mGridView.setAdapter(adapter);
        if (type == 1) {
            adapter.setMaxNumber(1);
            if (path != null) adapter.addVideo(path);
            mTopTitle.setText("发布短视频");
            mContenEdt.setMaxLines(2);
            videoUploadFileList = new ArrayList<>();
        } else {
            imageUploadFileList = new ArrayList<>();
        }
        PictureManager.getInstance().setCallback(this);
        mloginCheck = new IsLoginCheck(getActivity());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                if (mIsLongPres) return;
                if (arg2 == adapter.getSize()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
                    pop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                } else {
                    int nCount = adapter.getSize();
                    if (nCount > 0) {
                        MediaModel item = (MediaModel) adapter.getItem(arg2);
                        if (item.type == 0) {
                            OpenImageUtils.openImage(getContext(), item.url, arg2);
                        } else if (item.type == 1) {
                            Intent s = new Intent(getContext(), ComunityFullVideoActivity.class);
                            s.putExtra("url", item.url);
                            startActivity(s);
                        } else {
                            return;
                        }
                    }
                }
            }
        });
        mTopicalApi = new TopicalApi(getActivity());
        mProcessDialog = new ProcessDialog(getActivity());
        mProcessDialog.set_onCloseLister(new ProcessDialog.OnCancelBtnLister() {
            @Override
            public void onClosed() {
                App.getInstance().setUpFileFlag(false);
                closeProcessDialog();
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("提交中");
        init();
        initColumns();
    }

    public void obtainUpResult() {
        isCopmlate = true;
        if (PictureManager.getInstance().isUpSuccess()) {
            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
            Toast.makeText(getActivity(), "发布主题成功", Toast.LENGTH_LONG).show();
            if (getActivity() != null) {
                closeProcessDialog();
                getActivity().finish();
            }
        } else {
            if (getActivity() != null) {
                closeProcessDialog();
                String msg = PictureManager.getInstance().getErrMsg();
                Toast.makeText(getActivity(), "发布主题失败：" + msg, Toast.LENGTH_LONG).show();
            }
        }
        PictureManager.getInstance().clearCache();
    }

    public void closeProcessDialog() {
        isCopmlate = true;
        if (mProcessDialog != null && mProcessDialog.isShowActivty())
            mProcessDialog.dismissDialog();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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

    public void init() {
        int unSelectedColor = getResources().getColor(R.color.label_text_color);
        int selectedColor = getResources().getColor(R.color.white);
        int unSelectedRes = R.drawable.shape_label_box;
        int selectedRes = R.drawable.shape_label_selected_box;
        mTabGrouplayout.changeThemeForTextColor(selectedColor, unSelectedColor, selectedRes, unSelectedRes);
        mTabGrouplayout.setAddFlagNeedShown(false);
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
        View bt4 = (Button) view.findViewById(R.id.item_popupwindows_video);
//        Button bt5 = (Button) view.findViewById(R.id.item_popupwindows_audio);
        if (type == 1) {
            ((View) bt4.getParent()).setVisibility(View.VISIBLE);
            ((View) bt1.getParent()).setVisibility(View.GONE);
            ((View) bt2.getParent()).setVisibility(View.GONE);
        }

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
                hideInputSoft(mContenEdt);
                gotoTakeImage();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideInputSoft(mContenEdt);
                int max = 9;
                if (adapter.getAllItems() != null) {
                    int count = Math.min(max - adapter.getAllItems().size(), max);
                    max = count;
                }
                gotoSelectImage(false, max);
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
                hideInputSoft(mContenEdt);
                gotoSelectVideo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    public void initColumns() {
        if (mColumnId == -1) return;
        Observable.just(mColumnId)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, List<String>>() {
                    @Override
                    public List<String> call(Long id) {
                        TopicalEntry entry = mTopicalApi.getTopicAllColumns(id);
                        return entry.getTags();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> data) {
                        if (data != null && !data.isEmpty()) {
                            mTotakLabels = data;
                        } else {
                            mAddTabLabel.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MediaChooserConstants.SELECTED_MEDIA_COUNT = 0;
    }

    @Override
    public void onSelectImagesData(ArrayList<MediaModel> list) {
        if (list != null && !list.isEmpty()) {
            for (MediaModel model : list) {
                adapter.addTail(model);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSelectVideosData(ArrayList<MediaModel> list) {
        if (list != null && !list.isEmpty()) {
            for (MediaModel model : list) {
                adapter.addTail(model);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case 7016: {
                    mSelectLabels = data.getStringArrayListExtra(WhiteTopBarActivity.KEY_PARAM);
                    if (mSelectLabels != null && !mSelectLabels.isEmpty()) {
                        mTabGrouplayout.removeAllViews();
                        mTabGrouplayout.setVisibility(View.VISIBLE);
                        mTabGrouplayout.addAllBody(mSelectLabels.toArray(new String[0]));
                    } else {
                        mTabGrouplayout.setVisibility(View.GONE);
                    }
                }
                break;
            }
            getActivity().setResult(1, data);
        }
        super.onActivityResult(requestCode, resultCode, data);    //这句话很重要
    }

    @Override
    public void onTextStatusCHanged(boolean isHas) {
        if (isHas) {
            mPublishBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_send_select));
        } else {
            mPublishBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_send_normal));
        }
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
        if (view == backBtn) {
            hideInputSoft(mContenEdt);
            getActivity().finish();
        } else if (view == mAddTabLabel) {
            Intent intent = new Intent(getActivity(), WhiteTopBarActivity.class);
            intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME, CommunityLabelFragment.class.getName());
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "选择标签");
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_RIGHT_TEXT, "确定");
            intent.putExtra(WhiteTopBarActivity.KEY_PARAM, (Serializable) mTotakLabels);
            intent.putExtra(WhiteTopBarActivity.KEY_PARAM2, (Serializable) mSelectLabels);
            startActivityForResult(intent, 7016);
        } else if (view == mPublishBtn) {
            if (!mloginCheck.checkLogin()) return;
            if (TextUtils.equals(mContenEdt.getText(), "")) {
                Toast.makeText(getActivity(), "发表内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            hideInputSoft(mContenEdt);
            if (!isCopmlate) return;
            isCopmlate = false;
//            progressDialog.show();
//            mProcessDialog.showDialog(rootView);
//            mProcessDialog.updateValues("cd",10);
            ArrayList<MediaModel> arr = adapter.getAllItems();
            if (arr != null && !arr.isEmpty()) {
//                PictureManager.getInstance().clearCache();
                if (type == 0) {
                    //image
//                    progressDialog.show();
//                    mProcessDialog.showDialog(rootView);
//                    PictureManager.getInstance().compressionImage(arr);
                    postWord(arr);
                } else {
                    //video
//                    mProcessDialog.showDialog(rootView);
//                    PictureManager.getInstance().setmDllist(arr);
//                    exeUploadTast();
                    postWord(arr);
                }
            } else {
                // 传文本
                progressDialog.show();
//                exeUploadTast();
                createTopical();
//                postWord(arr);
            }
        }
    }

    private void exeUploadTast() {
        UploadAttment<ArrayList<String>> uploadEntry = new UploadAttment<ArrayList<String>>();
        String content = mContenEdt.getText().toString().trim();
        uploadEntry.setId(mColumnId);
        uploadEntry.setContent(content);
        uploadEntry.setParam((ArrayList<String>) mSelectLabels);
        Intent intent = new Intent(getActivity(), MyFileUpService.class);
        intent.putExtra("param", uploadEntry);
        //发送数据给service
        getActivity().startService(intent);
    }

    public void createTopical() {
        isCopmlate = false;
        String title = mTitleEdt.getText().toString().trim();
        String content = mContenEdt.getText().toString().trim();
        ArrayList<MediaModel> list = PictureManager.getInstance().getmDllist();
        mTopicalApi.publisTopic(mColumnId, content, list, mSelectLabels, new ProgessCallbacface() {
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
                closeProcessDialog();
                PictureManager.getInstance().clearCache();
            }

            @Override
            public void onNext(Long aLong) {
                closeProcessDialog();
                if (aLong != -1) {
                    RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
                    Toast.makeText(getActivity(), "发布主题成功", Toast.LENGTH_LONG).show();
                    PictureManager.getInstance().clearCache();
                    getActivity().finish();
                }
            }
        });
    }

    private void postWord(ArrayList<MediaModel> list) {
        Observable.just(list)
                .observeOn(Schedulers.io())
                .map(new Func1<ArrayList<MediaModel>, ArrayList<UploadFileData>>() {
                    @Override
                    public ArrayList<UploadFileData> call(ArrayList<MediaModel> dlist) {
                        try {
                            ArrayList<UploadFileData> upList = new ArrayList<>();
                            if (type == 1 && dlist != null) {
                                String videoUrl = mTopicalApi.getVideoUpfileUrl();
                                Log.e("TAG", "video upload Url === " + videoUrl);
                                for (MediaModel upF : dlist) {
                                    if (!TextUtils.isEmpty(upF.url)) {
                                        File file = new File(upF.url);
                                        Log.e("TAG", "video == " + upF.url);
                                        if (file.exists()) {
                                            UploadFileData data = UploadFileData.videoUploadFile(videoUrl, file);
                                            upList.add(data);
                                        }
                                    }
                                }
                            } else {
                                if (dlist != null) {
                                    String imageUrl = mTopicalApi.getImaUpfileUrl();
                                    Log.e("TAG", "image upload Url === " + imageUrl);
                                    for (MediaModel upF : dlist) {
                                        if (!TextUtils.isEmpty(upF.url)) {
                                            File file = new File(upF.url);
                                            Log.e("TAG", "image == " + upF.url);
                                            if (file.exists()) {
                                                UploadFileData data = UploadFileData.imageUploadFile(imageUrl, file);
                                                upList.add(data);
                                            }
                                        }
                                    }
                                }
                            }
                            return upList;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<UploadFileData>>() {
                    @Override
                    public void call(ArrayList<UploadFileData> uploadFileData) {
                        if (uploadFileData != null) {
                            PublishData<String> data = new PublishData<>();
                            String content = mContenEdt.getText().toString().trim();
                            data.setData(content);
                            data.setUploadFileDataList(uploadFileData);
                            PublishDataManager.getInstance().post(data);
                            data.setPushAction(new PublishData.OnPublishDataAction<String>() {
                                @Override
                                public boolean onPublishData(ArrayList<UploadFileData> uploadFileResultList, String data) {
                                    if (uploadFileResultList != null &&
                                            !uploadFileResultList.isEmpty()) {
                                        JSONArray arr = new JSONArray();
                                        for (int i = 0; i < uploadFileResultList.size(); i++) {
                                            UploadFileData model = uploadFileResultList.get(i);
                                            try {
                                                JSONObject ob = new JSONObject();
                                                JSONArray yy = new JSONArray();
                                                yy.put(model.getResult().getServicePath());
                                                ob.put("name", model.getResult().getServerName());
                                                ob.put("paths", yy);
                                                arr.put(ob);
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                        JSONArray tagsArr = new JSONArray();
                                        if (mSelectLabels != null && mSelectLabels.size() > 0) {
                                            for (String str : mSelectLabels) {
                                                tagsArr.put(str);
                                            }
                                        }
                                        return pubTopic(data, arr, tagsArr);
                                    }
                                    return false;
                                }
                            });
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            Toast.makeText(getActivity(), "获取上传地址失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public final static String TAG = "TAG";

    public boolean pubTopic(String content, JSONArray att, JSONArray tags) {
        boolean isOk = false;
        JSONObject parmaObj = new JSONObject();
        try {
            parmaObj.put("title", "");
            parmaObj.put("content", content);
            parmaObj.put("attachment_infos", att);
            parmaObj.put("tags", tags);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, parmaObj.toString());
        String url = App.getInstance().getmSession().getCommunityServerUrl() + "/public/columns/" + mColumnId + "/threads";
        JSONObject result = JsonHelper.httpPostJson(url, parmaObj, App.getInstance().getCurrentToken());
        long topicId = -1;
        if (result != null && !TextUtils.isEmpty(result.toString())) {
            topicId = result.optLong("id");
            Log.e(TAG, "topicId=====" + topicId);
            if (topicId != -1) {
                isOk = true;
                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_UPDTA_ATTEION_OK));
            }
        }
        return isOk;
    }

    @Override
    public void getData(ArrayList<MediaModel> list) {
        if (getActivity() == null) return;
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(PictureManager.getInstance().saveDirTemp)));
//        exeUploadTast();
        postWord(list);
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
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        return BackHandlerHelper.handleBackPress(this);
    }
}