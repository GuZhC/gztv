package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.util.Log;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwb on 2016/12/29.
 */
public class UserTradeLogHelper {

    private MyDataManager dataManager;
    private String platTradeUrl;
    private String liveTradeurl;
    private Object object = new Object();

    public UserTradeLogHelper(Context context) {
        dataManager = new MyDataManager(context);
        platTradeUrl = App.getInstance().getmSession().getPortalServerUrl() + "/public/trade-actions";
        liveTradeurl = App.getInstance().getmSession().getLiveServerUrl() + "/public/trade-actions";
    }

    public void getTradeRecordList(int page, int size, final DataRequest.DataCallback<TradeRecords> callback) {
        getTradeRecordList(page, size, 2, callback);
    }

    /**
     * <交易类型：0 – 不限, 1 – 收入, 2 – 支出, 3 – 支付>
     *
     * @param page
     * @param size
     * @param type
     * @param callback
     */
    public void getTradeRecordList(int page, int size, int type, final DataRequest.DataCallback<TradeRecords> callback) {
        dataManager.getMyTradeRecordList(page, size, type, new DataRequest.DataCallbackTag<TradeRecords>() {
            @Override
            public void onSuccess(final Object object, final boolean isAppend, TradeRecords data) {
                Observable.just(data)
                        .observeOn(Schedulers.computation())
                        .flatMap(new Func1<TradeRecords, Observable<TempTradeRecord>>() {
                            @Override
                            public Observable<TempTradeRecord> call(TradeRecords tradeRecords) {
                                if (tradeRecords != null && tradeRecords.getData() != null) {
                                    TempTradeRecord tempTradeRecord = new TempTradeRecord();
                                    tempTradeRecord.tradeRecords = tradeRecords;
                                    JSONObject platJson = new JSONObject();
                                    JSONObject liveJson = new JSONObject();
                                    HashMap<String, HashMap<Long, TradeRecords.TradeRecordItem>> actionMap =
                                            new HashMap<String, HashMap<Long, TradeRecords.TradeRecordItem>>();
                                    tempTradeRecord.platformActionRequestJson = platJson;
                                    tempTradeRecord.liveActionRequestJson = liveJson;
                                    tempTradeRecord.actionMap = actionMap;
                                    try {
                                        for (TradeRecords.TradeRecordItem recordItem : tradeRecords.getData()) {
                                            String action = recordItem.getAction();
                                            String actionKey = tradeActionToActionKey(action);

                                            HashMap<Long, TradeRecords.TradeRecordItem> itemIdmap = null;
                                            itemIdmap = actionMap.get(actionKey);
                                            if (itemIdmap == null) {
                                                itemIdmap = new HashMap<Long, TradeRecords.TradeRecordItem>();
                                                actionMap.put(actionKey, itemIdmap);
                                            }
                                            itemIdmap.put(recordItem.getSource_detail_id(), recordItem);

                                            JSONArray actionArr = null;
                                            if (isLiveTradeAction(action)) {
                                                actionArr = liveJson.optJSONArray(actionKey);
                                                if (actionArr == null) {
                                                    actionArr = new JSONArray();
                                                    liveJson.put(actionKey, actionArr);
                                                }
                                                actionArr.put(recordItem.getSource_detail_id());
                                            } else if (isPlatFormTradeAction(action)) {
                                                actionArr = platJson.optJSONArray(actionKey);
                                                if (actionArr == null) {
                                                    actionArr = new JSONArray();
                                                    platJson.put(actionKey, actionArr);
                                                }
                                                actionArr.put(recordItem.getSource_detail_id());
                                            }
                                        }
                                        Log.e("TAG", "trade live json == " + liveJson.toString());
                                        Log.e("TAG", "trade plat json == " + platJson.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return Observable.just(tempTradeRecord);
                                }
                                return Observable.just(null);
                            }
                        })
                        .observeOn(Schedulers.io())
                        .map(new Func1<TempTradeRecord, TradeRecords>() {
                            @Override
                            public TradeRecords call(TempTradeRecord tempTradeRecord) {
                                if (tempTradeRecord != null) {
                                    TradeRecords tradeRecords = tempTradeRecord.tradeRecords;
                                    //                                    ArrayList<TradeRecords.TradeRecordItem> itemList = new ArrayList<TradeRecords.TradeRecordItem>();
                                    if (tempTradeRecord.liveActionRequestJson != null &&
                                            tempTradeRecord.liveActionRequestJson.toString().length() > 2) {
                                        Log.e("TAG", "post live action ==" + liveTradeurl);
                                        String liveTradeRes = HttpUtil.execute(liveTradeurl,
                                                new HttpParameters(tempTradeRecord.liveActionRequestJson),
                                                App.getInstance().getCurrentToken());
                                        Log.e("TAG", "liveTradeRes == " + liveTradeRes);
                                        parserTradeActionDetails(tempTradeRecord, liveTradeRes);
                                    }
                                    if (tempTradeRecord.platformActionRequestJson != null &&
                                            tempTradeRecord.platformActionRequestJson.toString().length() > 2) {
                                        Log.e("TAG", "post plat action ==" + platTradeUrl);
                                        String platTradeRes = HttpUtil.execute(platTradeUrl,
                                                new HttpParameters(tempTradeRecord.platformActionRequestJson),
                                                App.getInstance().getCurrentToken());
                                        Log.e("TAG", "platTradeRes == " + platTradeRes);
                                        parserTradeActionDetails(tempTradeRecord, platTradeRes);
                                    }
                                    removeNoTradeActionItem(tradeRecords);
                                    return tradeRecords;
                                }

                                return null;
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<TradeRecords>() {
                            @Override
                            public void call(TradeRecords tradeRecords) {
                                if (callback instanceof DataRequest.DataCallbackTag) {
                                    ((DataRequest.DataCallbackTag) callback)
                                            .onSuccess(object, isAppend, tradeRecords);
                                }
                                callback.onSuccess(isAppend, tradeRecords);
                            }
                        });
            }

            @Override
            public void onSuccess(boolean isAppend, TradeRecords data) {

            }

            @Override
            public void onFail(ApiException e) {
                callback.onFail(e);
            }
        });
    }

    /**
     * 删除空数据
     *
     * @param tradeRecords
     */
    private void removeNoTradeActionItem(TradeRecords tradeRecords) {
        if (tradeRecords != null && tradeRecords.getData() != null) {
            synchronized (object) {
                for (int i = 0; i < tradeRecords.getData().size(); i++) {
                    TradeRecords.TradeRecordItem item = tradeRecords.getData().get(i);
                    if (item.getTradeActionInfo() == null || item.getTradeActionInfo().getTAId() == 0) {
                        tradeRecords.getData().remove(i);
                        i--;
                    }
                }
            }
        }
    }

    private String tradeActionToActionKey(String action) {
        String actionKey;
        if (action.endsWith("s")) {
            actionKey = action;
        } else {
            actionKey = action + "s";
        }
        return actionKey;
    }

    private void parserTradeActionDetails(TempTradeRecord tempTradeRecord, String liveTradeRes) {
        try {
            StringUtil.checkHttpResponseError(liveTradeRes);
            JSONObject liveResJson = new JSONObject(liveTradeRes);
            for (Map.Entry<String, HashMap<Long, TradeRecords.TradeRecordItem>> entry : tempTradeRecord.actionMap.entrySet()) {
                String actionKey = entry.getKey();
                HashMap<Long, TradeRecords.TradeRecordItem> idMap = entry.getValue();
                JSONArray array = liveResJson.optJSONArray(actionKey);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.optJSONObject(i);
                        TradeAction tradeA = createTradeActionByActionResponse(actionKey, item.toString());
                        if (tradeA != null) {
                            if (idMap.get(tradeA.getTAId()) != null) {
                                idMap.get(tradeA.getTAId()).setTradeActionInfo(tradeA);
                            }
                        }
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TradeAction createTradeActionByActionResponse(String actionKey, String res) {
        TradeAction ra = null;
        Gson gson = new Gson();
        if ("charges".equals(actionKey)) {
            ra = gson.fromJson(res, ChargeAction.class);
        } else if ("transfers".equals(actionKey)) {
            ra = gson.fromJson(res, TransferAction.class);
        } else if ("increases".equals(actionKey)) {
            ra = gson.fromJson(res, IncreaseAction.class);
        } else if ("decreases".equals(actionKey)) {
            ra = gson.fromJson(res, DecreaseAction.class);
        } else if ("moves".equals(actionKey)) {
            ra = gson.fromJson(res, MoveAction.class);
        } else if ("send_gifts".equals(actionKey)) {
            ra = gson.fromJson(res, SendGiftAction.class);
        } else if ("bet_shows".equals(actionKey)) {
            ra = gson.fromJson(res, BetShowsAction.class);
        } else if ("bet_bonus".equals(actionKey)) {
            ra = gson.fromJson(res, BetBonusAction.class);
        }
        return ra;
    }

    private boolean isPlatFormTradeAction(String action) {
        if ("charge".equals(action) ||
                "transfer".equals(action) ||
                "increase".equals(action) ||
                "decrease".equals(action) ||
                "move".equals(action)) {
            return true;
        }
        return false;
    }

    private boolean isLiveTradeAction(String action) {
        if ("send_gift".equals(action) ||
                "bet_show".equals(action) ||
                "bet_bonus".equals(action)) {
            return true;
        }
        return false;
    }

    class TempTradeRecord {
        TradeRecords tradeRecords;
        /**
         * 用action 做key， HashMap<Long, TradeRecords.TradeRecordItem>为值
         * <p>
         * HashMap<Long, TradeRecords.TradeRecordItem> 中 用TradeRecordItem的source_detail_id为Key
         */
        HashMap<String, HashMap<Long, TradeRecords.TradeRecordItem>> actionMap;

        /**
         * 直播的交易请求参数
         */
        JSONObject liveActionRequestJson;

        /**
         * 平台的交易请求参数
         */
        JSONObject platformActionRequestJson;

    }
}
