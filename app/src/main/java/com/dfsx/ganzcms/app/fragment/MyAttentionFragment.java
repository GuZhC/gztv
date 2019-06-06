package com.dfsx.ganzcms.app.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.Follows;
import com.dfsx.ganzcms.app.view.TwoRelyView;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.RXBusUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenxiaolong on 2016/11/1.
 * 我的关注
 */
public class MyAttentionFragment extends AbsMyAttentionFansFragment {

    private PopupWindow popupWindow;
    private ImageView popLogoImage;
    private TextView popNameNoteText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPop();
    }


    private ArrayList<Integer> createStateArray(List<Follows.DataBean> list) {
        ArrayList<Integer> array = new ArrayList<>(list.size());
        for (Follows.DataBean bean : list) {
            if (bean.isFanned())
                array.add(MODE_MUTUAL_ATTENTION);
            else
                array.add(MODE_ATTENTION);
        }
        return array;
    }

    private void showPopView() {
        if (popupWindow != null) {
            try {
                IConcernData data = getAdapterData().get(getClicedPosition());
                LSLiveUtils.showUserLogoImage(context, popLogoImage, data.getLogoUrl());
                popNameNoteText.setText(getNoteText(data.getNickName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupWindow.showAtLocation(getView().findViewById(R.id.frag_list_view), Gravity.BOTTOM, 0, 0);
        }
    }

    private void initPop() {
        View pop = LayoutInflater.from(context).inflate(R.layout.my_attention_bottom_view, null);
        popupWindow = new PopupWindow(pop, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);

        initPopAction(popupWindow, pop);
    }


    private String getNoteText(String name) {
        return "你将不再关注@" + name + "?";
    }

    private void initPopAction(final PopupWindow popupWindow, final View pop) {
        TextView noAttention = (TextView) pop.findViewById(R.id.no_attention);
        TextView cancle = (TextView) pop.findViewById(R.id.cancle);
        popLogoImage = (ImageView) pop.findViewById(R.id.user_logo_image);
        popNameNoteText = (TextView) pop.findViewById(R.id.user_name_note_text);
        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        noAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                setState(getClicedPosition(), MODE_NO_ATTENTION);
                setNoAttentionView(getClickedTworelyView());


                LiveChannelManager manager = new LiveChannelManager(getActivity());
                manager.removeConcern(getAdapterData().get(getClicedPosition()).getUserId(), new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        showShortToast("取消关注成功");
                        getAdapterData().remove(getClicedPosition());
                        getListAdapter().notifyDataSetChanged();
                        RXBusUtil.sendConcernChangeMessage(false, 1);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        showShortToast("取消关注失败");
                        reverseState(getClickedTworelyView(), getClicedPosition());
                    }
                });

                //                topicalApi.attentionAuthor(getAdapterData().get(getClicedPosition()).getId(), 1, new DataRequest.DataCallback() {
                //                    @Override
                //                    public void onSuccess(boolean isAppend, Object data) {
                //                        if (!(Boolean) data)
                //                            onFail(new ApiException("服务器提示出错"));
                //                        else {
                //                            showShortToast("取消关注成功");
                //                            getAdapterData().remove(getClicedPosition());
                //                            getListAdapter().notifyDataSetChanged();
                //                            RXBusUtil.sendConcernChangeMessage(false, 1);
                //                        }
                //                    }
                //
                //                    @Override
                //                    public void onFail(ApiException e) {
                //                        showShortToast("取消关注失败");
                //                        reverseState(getClickedTworelyView(), getClicedPosition());
                //                    }
                //                });
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }


    @Override
    protected void reverseState(TwoRelyView twoRelyView, int position) {
        int state = getState(position);
        if (state == MODE_ATTENTION) {
            setState(position, MODE_NO_ATTENTION);
            setNoAttentionView(twoRelyView);
        } else if (state == MODE_NO_ATTENTION) {
            setState(position, MODE_ATTENTION);
            setAttentionView(twoRelyView);
        }
    }

    @Override
    protected void onAttentionViewClick(TwoRelyView twoRelyView, int position) {
        int state = getState(position);
        if (state == MODE_ATTENTION || state == MODE_MUTUAL_ATTENTION) {
            showPopView();
        } else {
            setState(position, MODE_ATTENTION);
            setAttentionView(twoRelyView);


            LiveChannelManager manager = new LiveChannelManager(getActivity());
            manager.addConcern(getAdapterData().get(getClicedPosition()).getUserId(), new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    showShortToast("关注成功");
                    RXBusUtil.sendConcernChangeMessage(true, 1);
                }

                @Override
                public void onFail(ApiException e) {
                    showShortToast("关注失败");
                }
            });


            //            topicalApi.attentionAuthor(getAdapterData().get(position).getId(), 0, new DataRequest.DataCallback() {
            //                @Override
            //                public void onSuccess(boolean isAppend, Object data) {
            //                    if (!(Boolean) data)
            //                        onFail(new ApiException("服务器提示出错"));
            //                    else {
            //                        showShortToast("关注成功");
            //                        RXBusUtil.sendConcernChangeMessage(true, 1);
            //                    }
            //                }
            //
            //                @Override
            //                public void onFail(ApiException e) {
            //                    showShortToast("关注失败");
            //                }
            //            });


        }
    }


    /**
     * 得到 "id1,id2,id3"这样的字符串
     *
     * @param list
     * @return
     */
    String getIds(List<Follows.DataBean> list) {
        List<String> ids = new ArrayList<>();
        String result = "";
        for (Follows.DataBean bean : list)
            result += "," + bean.getFollow_user_id();
        return result.substring(1);
    }

    @Override
    protected void getDataFromNetWork(final List<IConcernData> data,
                                      final MyAdapter adapter, final boolean isAppend, int index) {
        //        if (progressDialog != null)
        //            progressDialog.show();
        final String getUsersInfoUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/multiple/";

        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/" + userId + "/follows?page=" + index + "&size=" + numberPerPage;
        subscription = Observable.just(url).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).flatMap(new Func1<String, Observable<List<Follows.DataBean>>>() {
            @Override
            public Observable<List<Follows.DataBean>> call(String s) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = JsonHelper.httpGetJson(s, null);
                } catch (Exception e) {
                    return Observable.error(new Exception("获取关注失败"));
                }
                if (jsonObject != null && jsonObject.optString("error").equals("500")) {
                    return Observable.error(new ApiException("服务器返回500"));
                }

                List<Follows.DataBean> list = new Gson().fromJson(jsonObject.toString(), Follows.class).getData();

                if (list.size() == 0)
                    return Observable.error(new ApiException("没有更多数据"));
                return Observable.just(list);
            }
        }).flatMap(new Func1<List<Follows.DataBean>, Observable<?>>() {
            @Override
            public Observable<?> call(List<Follows.DataBean> list) {
                try {
                    String url = getUsersInfoUrl + getIds(list);
                    JSONObject jsonObject = JsonHelper.httpGetJson(url, null);
                    Account.UserBean[] userBeens = new Gson().fromJson(jsonObject.optString("result"), Account.UserBean[].class);
                    if (!isAppend)
                        data.clear();

                    ArrayList<IConcernData> arrayList = new ArrayList<IConcernData>();
                    for (int i = 0; i < list.size(); i++) {
                        Follows.DataBean dataBean = list.get(i);
                        if (dataBean != null) {
                            if (i >= 0 && i < userBeens.length && userBeens[i].getId() == dataBean.getFollow_user_id()) {
                                dataBean.setSignature(userBeens[i].getSignature());
                            }
                        }
                        arrayList.add(dataBean);
                    }
                    data.addAll(arrayList);
                } catch (Exception e) {
                    return Observable.error(e);
                }
                return null;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                pullToRefreshListView.onRefreshComplete();
                adapter.notifyDataSetChanged();
                //                if (progressDialog != null)
                //                    progressDialog.dismiss();

                emptyView.loadOver();
            }

            @Override
            public void onError(Throwable e) {
                showLongToast(e.getMessage());
                //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                pullToRefreshListView.onRefreshComplete();

                emptyView.loadOver();
            }

            @Override
            public void onNext(Object o) {
                //                if (progressDialog != null)
                //                    progressDialog.dismiss();
                pullToRefreshListView.onRefreshComplete();

                emptyView.loadOver();
            }
        });
    }

}
