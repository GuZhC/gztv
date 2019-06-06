package com.dfsx.ganzcms.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.ToastUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.logonproject.act.LoginActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.adapter.GridViewAdapter;
import com.dfsx.ganzcms.app.business.PictureManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.Attachment;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.R;
import com.dfsx.openimage.OpenImageUtils;
import com.dfsx.selectedmedia.MediaModel;
import com.dfsx.selectedmedia.activity.VideoFragmentActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by heyang on 2016/11/25
 */
public class CommendPubFragment extends BaseLocaFileFragment implements View.OnClickListener {
    GridView mGridView = null;
    GridViewAdapter adapter;
    private boolean mIsLongPres = false;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    ImageButton backBtn;
    TextView mPublishBtn;
    ProgressDialog mProcessDialog;
    private long mColumnId = -1;
    EditText mContenEdt;
    TopicalApi mTopicalApi = null;
    ImageView mBrowerpicBtn;
    ImageView mTakeimgBtn;
    View rootView;
    ImageView mImagThumb;
    ImageView mImagDelThumb;
    long tId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.community_commend_activity, null);
        if (getArguments() != null) {
            mColumnId = getArguments().getLong(DefaultFragmentActivity.KEY_FRAGMENT_PARAM);
            tId = getArguments().getLong("tid");
        }
        mImagThumb = (ImageView) rootView.findViewById(R.id.commeng_ing_thumb);
        mImagThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = (String) mImagThumb.getTag(R.id.tag_commend_thumb);
                if (path != null && !TextUtils.isEmpty(path)) {
                    OpenImageUtils.openImage(getActivity(), path, 0);
                }
            }
        });
        mImagDelThumb = (ImageView) rootView.findViewById(R.id.commeng_ing_del_thumb);
        mImagDelThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //     mImagThumb.setTag(R.id.tag_commend_thumb, null);
//                UtilHelp.LoadNewsThumebImage(mImagThumb, "", null);
                mImagDelThumb.setVisibility(View.GONE);
                mImagThumb.setVisibility(View.INVISIBLE);
            }
        });
        mContenEdt = (EditText) rootView.findViewById(R.id.commit_content_edt);
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
//        InputMethodManager  inputMager= (InputMethodManager) mContenEdt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMager.showSoftInput(mContenEdt,0);
        backBtn = (ImageButton) rootView.findViewById(R.id.btn_retun);
        backBtn.setOnClickListener(this);
        mPublishBtn = (TextView) rootView.findViewById(R.id.commonity_publish_btn);
        mPublishBtn.setOnClickListener(this);
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
        mTopicalApi = new TopicalApi(getActivity());
        mProcessDialog = new ProgressDialog(getActivity());
        mProcessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProcessDialog.setMessage("提交中");
        init();
        return rootView;
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
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotoSelectImage(true);
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

        mBrowerpicBtn = (ImageView) rootView.findViewById(R.id.brower_img_btn);
        mBrowerpicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSelectImage(true);
            }
        });
        mTakeimgBtn = (ImageView) rootView.findViewById(R.id.take_photo_btn);
        mTakeimgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoTakeImage();
            }
        });
    }

    @Override
    public void onSelectImagesData(ArrayList<MediaModel> list) {
        if (list != null && list.size() > 0) {
            String fileUrString = list.get(0).url;
            mImagThumb.setVisibility(View.VISIBLE);
            mImagThumb.setTag(R.id.tag_commend_thumb, fileUrString);
            mImagDelThumb.setVisibility(View.VISIBLE);
            Util.LoadThumebImage(mImagThumb, fileUrString, null);
//            Glide.with(getActivity())
//                    .load(fileUrString)
//                    .override(150,150)
//                    .placeholder(R.color.white)
//                    .error(R.color.white)
//                    .centerCrop()
//                    .into(mImagThumb);
        } else {
            mImagDelThumb.setVisibility(View.GONE);
            mImagThumb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSelectVideosData(ArrayList<MediaModel> list) {

    }

    @Override
    public void onClick(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.image_click));
        if (view == backBtn) {
            hideInputSoft(mContenEdt);
            getActivity().finish();
        } else if (view == mPublishBtn) {
            if (TextUtils.isEmpty(mContenEdt.getText().toString())) {
                ToastUtils.toastNoContentCommendFunction(getActivity());
                return;
            }
            hideInputSoft(mContenEdt);
            if (App.getInstance().getUser() == null) {
                Toast.makeText(getActivity(), "尚未登录，请先登录", Toast.LENGTH_LONG).show();
                Intent inten = new Intent(getActivity(), LoginActivity.class);
                startActivity(inten);
                return;
            }
            postCommendContent();
        }
    }

    public void dimisssDialog() {
        if (mProcessDialog != null && mProcessDialog.isShowing())
            mProcessDialog.dismiss();
    }

    public void postCommendContent() {
        mProcessDialog.show();
        String imgpath = (String) mImagThumb.getTag(R.id.tag_commend_thumb);
        if (imgpath != null && !TextUtils.isEmpty(imgpath)) {
            PictureManager.getInstance().compressionSignImage(imgpath);
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(PictureManager.getInstance().saveDirTemp)));
            String temp = PictureManager.getInstance().getSignlPath();
            if (temp != null && !TextUtils.isEmpty(temp))
                imgpath = temp;
        }
        Observable.just(imgpath).
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.io()).
                flatMap(new Func1<String, Observable<Attachment>>() {
                    @Override
                    public Observable<Attachment> call(String filepath) {
                        Attachment att = null;
                        if (filepath != null && !TextUtils.isEmpty(filepath)) {
                            String upFIleUrl = "";
                            try {
                                upFIleUrl = mTopicalApi.getImaUpfileUrl();
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                            if (upFIleUrl != null && !TextUtils.isEmpty(upFIleUrl)) {
                                String sharPath = mTopicalApi.synchronNewUpfile(upFIleUrl, filepath);
                                String name = UtilHelp.getNameFromUrl(filepath);
                                if (sharPath != null && !TextUtils.isEmpty(sharPath)) {
                                    att = new Attachment();
                                    att.setName(name);
                                    att.setUrl(sharPath);
                                }
                            }
                        }
                        return Observable.just(att);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Attachment>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(Attachment att) {
                                   String content = mContenEdt.getText().toString().trim();
                                   mTopicalApi.createComment(mColumnId, tId,content, att, new DataRequest.DataCallback() {
                                       @Override
                                       public void onSuccess(boolean isAppend, Object data) {
                                           dimisssDialog();
                                           long id = (Long) data;
                                           if (id != -1) {
                                               RxBus.getInstance().post(new Intent(IntentUtil.ACTION_COMNITY_COMNEND_OK));
                                               Toast.makeText(getActivity(), "评论提交成功", Toast.LENGTH_SHORT).show();
                                               getActivity().finish();
                                               PictureManager.getInstance().clearCache();
                                           }
                                       }

                                       @Override
                                       public void onFail(ApiException e) {
                                           dimisssDialog();
                                           Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                           PictureManager.getInstance().clearCache();
                                       }
                                   });
                               }
                           }
                );
    }

}
