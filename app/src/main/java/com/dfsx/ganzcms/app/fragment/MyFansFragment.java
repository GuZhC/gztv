package com.dfsx.ganzcms.app.fragment;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.model.Fans;
import com.dfsx.ganzcms.app.view.TwoRelyView;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
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
 * 我的粉丝
 */
public class MyFansFragment extends AbsMyAttentionFansFragment {


    private ArrayList<Integer> createStateArray(List<Fans.DataBean> list) {
        ArrayList<Integer> array = new ArrayList<>(list.size());
        for (Fans.DataBean dataBean : list) {
            if (dataBean.isFollowed())
                array.add(AbsMyAttentionFansFragment.MODE_ATTENTION);
            else
                array.add(AbsMyAttentionFansFragment.MODE_NO_ATTENTION);
        }
        return array;
    }


    @Override
    protected void getDataFromNetWork(final List<IConcernData> data, final MyAdapter adapter, final boolean isAppend, int index) {
        //        if (progressDialog != null)
        //            progressDialog.show();
        if (CoreApp.getInstance().getUser() == null || CoreApp.getInstance().getUser().getUser() == null) {
            return;
        }
        String getFansUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/" + userId + "/fans?page=" + index + "&size=" + numberPerPage;
        final String getUsersInfoUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/multiple/";
        subscription = Observable.just(getFansUrl).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).
                flatMap(new Func1<String, Observable<List<Fans.DataBean>>>() {
                    @Override
                    public Observable<List<Fans.DataBean>> call(String s) {
                        List<Fans.DataBean> list = new ArrayList<>();
                        try {
                            JSONObject jsonObject = JsonHelper.httpGetJson(s, CoreApp.getInstance().getUser().getToken());
                            List<Fans.DataBean> li = new Gson().fromJson(jsonObject.toString(), Fans.class).getData();
                            if (li != null && li.size() > 0)
                                list.addAll(li);
                        } catch (Exception e) {
                            return Observable.error(e);
                        }
                        return Observable.just(list);
                    }
                }).flatMap(new Func1<List<Fans.DataBean>, Observable<?>>() {
            @Override
            public Observable<?> call(List<Fans.DataBean> list) {
                //构造用户关注状态列表
                if (list.size() == 0)
                    return Observable.error(new ApiException("没有更多数据"));
                try {
                    String url = getUsersInfoUrl + getIds(list);
                    JSONObject jsonObject = JsonHelper.httpGetJson(url, null);
                    Account.UserBean[] userBeens = new Gson().fromJson(jsonObject.optString("result"), Account.UserBean[].class);
                    if (!isAppend)
                        data.clear();
                    ArrayList<IConcernData> arrayList = new ArrayList<IConcernData>();
                    for (int i = 0; i < list.size(); i++) {
                        Fans.DataBean data = list.get(i);
                        if (data != null) {
                            if (i >= 0 && i < userBeens.length && userBeens[i].getId() == data.getFan_user_id()) {
                                data.setSignature(userBeens[i].getSignature());
                            }
                        }
                        arrayList.add(data);
                    }
                    data.addAll(arrayList);
                } catch (Exception e) {
                    return Observable.error(e);
                }
                return Observable.just(null);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                pullToRefreshListView.onRefreshComplete();
                adapter.notifyDataSetChanged();

                emptyView.loadOver();
            }

            @Override
            public void onError(Throwable e) {
                String msgText = e != null ? e.getMessage() : "获取粉丝信息失败";
                showLongToast(msgText);
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

    /**
     * 得到 "id1,id2,id3"这样的字符串
     *
     * @param list
     * @return
     */
    String getIds(List<Fans.DataBean> list) {
        List<String> ids = new ArrayList<>();
        String result = "";
        for (Fans.DataBean bean : list)
            result += "," + bean.getFan_user_id();
        return result.substring(1);
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
        if (state == MODE_ATTENTION) {


            LiveChannelManager manager = new LiveChannelManager(getActivity());
            manager.removeConcern(getAdapterData().get(getClicedPosition()).getUserId(), new DataRequest.DataCallback<Boolean>() {
                @Override
                public void onSuccess(boolean isAppend, Boolean data) {
                    showShortToast("取消关注成功");
                    RXBusUtil.sendConcernChangeMessage(false, 1);
                }

                @Override
                public void onFail(ApiException e) {
                    showShortToast("取消关注失败");
                    reverseState(getClickedTworelyView(), getClicedPosition());
                }
            });


            //            topicalApi.attentionAuthor(getAdapterData().get(position).getId(), 1, new DataRequest.DataCallback() {
            //                @Override
            //                public void onSuccess(boolean isAppend, Object data) {
            //                    if (!(Boolean) data)
            //                        onFail(new ApiException("服务器提示出错"));
            //                    else {
            //                        {
            //                            showShortToast("取消关注成功");
            //                            RXBusUtil.sendConcernChangeMessage(false, 1);
            //                        }
            //                    }
            //                }
            //
            //                @Override
            //                public void onFail(ApiException e) {
            //                    showShortToast("取消关注失败");
            //                    reverseState(getClickedTworelyView(), getClicedPosition());
            //                }
            //            });


        } else {


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
                    reverseState(getClickedTworelyView(), getClicedPosition());
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
            //                    reverseState(getClickedTworelyView(), getClicedPosition());
            //                }
            //            });
        }
        reverseState(twoRelyView, position);
    }
}
