package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Messages;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 推送消息帮助文件
 */
public class PushMessageHelper {

    private static PushMessageHelper instance = new PushMessageHelper();

    private NoReadPushMessage noReadPushMessage;

    private ArrayList<NoReadPushMessageChangeListener> noReadPushMessageChangeListeners = new ArrayList<>();

    public static PushMessageHelper getInstance() {
        return instance;
    }

    public void getMessages(Context context, int type,
                            int state, int page, int size, boolean isAppend, DataRequest.DataCallback<Messages> callback) {
        PushApi.getMessages(context, type, state, page, size, isAppend, callback);
    }


    public void getNoReadMessageInfo(Context context, boolean isReGet, final DataRequest.DataCallback<NoReadPushMessage> callback) {
        if (!isReGet && noReadPushMessage != null) {
            if (callback != null) {
                callback.onSuccess(false, noReadPushMessage);
            }
        } else {
            Observable.just(context)
                    .observeOn(Schedulers.io())
                    .map(new Func1<Context, NoReadPushMessage>() {
                        @Override
                        public NoReadPushMessage call(Context context) {
                            Messages all = PushApi.getMessagesSync(context, 0, 1, 1, 1);
                            if (all != null && all.getTotal() > 0) {
                                NoReadPushMessage noReadM = new NoReadPushMessage((int) all.getTotal());
                                ArrayList<NoReadCategoryMessage> categoryMessageArrayList = new ArrayList<>();
                                noReadM.messageCategoryList = categoryMessageArrayList;
                                //category={消息类型：0-获取全部,1-系统类消息,2-关注类消息 }
                                for (int i = 1; i < 3; i++) {
                                    Messages itemMessages = PushApi.getMessagesSync(context, i, 1, 1, 1);
                                    if (itemMessages != null && itemMessages.getTotal() > 0) {
                                        categoryMessageArrayList.add(new
                                                NoReadCategoryMessage(itemMessages.getTotal(), i));
                                    }
                                }
                                return noReadM;
                            }
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<NoReadPushMessage>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (callback != null) {
                                callback.onFail(new ApiException(e));
                            }
                            noReadPushMessage = null;
                        }

                        @Override
                        public void onNext(NoReadPushMessage noReadMessage) {
                            noReadPushMessage = noReadMessage;
                            if (callback != null) {
                                callback.onSuccess(false, noReadMessage);
                            }
                            notifyNoReadMessage();
                        }
                    });
        }
    }

    /**
     * @param messageType 消息类型：0或不传-清理全部, 1-系统类消息, 2-关注类消息
     */
    public void tagAllMessageRead(int messageType) {
        Observable.just(messageType)
                .observeOn(Schedulers.io())
                .map(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer type) {
                        String url = App.getInstance().getmSession().getPortalServerUrl()
                                + "/public/users/current/messages/read?category=" + type;
                        String res = HttpUtil.execute(url, new HttpParameters(),
                                App.getInstance().getCurrentToken());
                        return TextUtils.isEmpty(res);
                    }
                })
                .subscribe();
    }

    public void addNoReadPushMessageChangeListener(NoReadPushMessageChangeListener listener) {
        noReadPushMessageChangeListeners.add(listener);
    }

    public void removeNoReadPushMessageChangeListener(NoReadPushMessageChangeListener listener) {
        noReadPushMessageChangeListeners.remove(listener);
    }

    private void notifyNoReadMessage() {
        for (NoReadPushMessageChangeListener listener1 : noReadPushMessageChangeListeners) {
            listener1.onNoReadMessage(noReadPushMessage);
        }
    }

    public void tagMessageRead(long... messageIds) {
        if (messageIds == null) {
            return;
        }
        Observable.just(messageIds)
                .observeOn(Schedulers.io())
                .map(new Func1<long[], Boolean>() {
                    @Override
                    public Boolean call(long[] longs) {
                        String url = App.getInstance().getmSession().getPortalServerUrl()
                                + "/public/users/current/messages/";
                        String ids = "";
                        for (int i = 0; i < longs.length; i++) {
                            long id = longs[i];
                            if (i == longs.length - 1) {
                                ids += id + "";
                            } else {
                                ids += id + ",";
                            }
                        }
                        url = url + ids + "/read";
                        String res = HttpUtil.execute(url, new HttpParameters(),
                                App.getInstance().getCurrentToken());
                        return TextUtils.isEmpty(res);
                    }
                })
                .subscribe();
    }

    public interface NoReadPushMessageChangeListener {

        void onNoReadMessage(NoReadPushMessage noReadPushMessageInfo);
    }

    public static class NoReadPushMessage {

        public int allCount;
        /**
         * 未读消息类型集合
         */
        private List<NoReadCategoryMessage> messageCategoryList;

        public NoReadPushMessage() {

        }

        public NoReadPushMessage(int num) {
            this.allCount = num;
        }

        public int getAllCount() {
            return allCount;
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }

        public List<NoReadCategoryMessage> getMessageCategoryList() {
            return messageCategoryList;
        }

        public void setMessageCategoryList(List<NoReadCategoryMessage> messageCategoryList) {
            this.messageCategoryList = messageCategoryList;
        }

    }

    public static class NoReadCategoryMessage {
        private long noReadCount;
        private int category;

        public NoReadCategoryMessage() {

        }

        public NoReadCategoryMessage(long noReadCount, int category) {
            this.noReadCount = noReadCount;
            this.category = category;
        }

        public long getNoReadCount() {
            return noReadCount;
        }

        public void setNoReadCount(long noReadCount) {
            this.noReadCount = noReadCount;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }
    }
}
