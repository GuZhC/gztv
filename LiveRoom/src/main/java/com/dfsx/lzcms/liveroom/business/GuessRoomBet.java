package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.lzcms.liveroom.model.BetOption;
import com.dfsx.lzcms.liveroom.model.BetResponse;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuwb on 2016/12/20.
 * 竞猜中的押注
 */
public class GuessRoomBet implements Bet {

    private long roomId;
    private String roomEnterId;

    public GuessRoomBet(long roomId, String roomEnterId) {
        this.roomId = roomId;
        this.roomEnterId = roomEnterId;
    }

    @Override
    public void betTeam(BetOption team, final double coins, final ICallBack<BetResponse> callBack) {
        Observable.just(team)
                .observeOn(Schedulers.io())
                .map(new Func1<BetOption, BetResponse>() {
                    @Override
                    public BetResponse call(BetOption betOption) {
                        String url = AppManager.getInstance().getIApp()
                                .getHttpBaseUrl() + "/public/shows/" + roomId + "/room/quiz/bet";
                        JSONObject postJson = new JSONObject();
                        try {
                            postJson.put("option_chosen", betOption.getId());
                            postJson.put("bet_coins", coins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HttpParameters parameters = new HttpParameters(postJson);
                        parameters.setHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId));

                        BetResponse response;

                        String res = HttpUtil.execute(url, parameters,
                                AppManager.getInstance().getIApp().getCurrentToken());
                        if (TextUtils.isEmpty(res)) {
                            response = new BetResponse(true);
                        } else {
                            response = new BetResponse(false);
                            try {
                                JSONObject resJson = new JSONObject(res);
                                response.setErrorCode(resJson.optInt("error"));
                                response.setErrorMsg(resJson.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        response.setBetOption(betOption);
                        response.setBetCoins(coins);
                        return response;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BetResponse>() {
                    @Override
                    public void call(BetResponse betResponse) {
                        if (callBack != null) {
                            callBack.callBack(betResponse);
                        }
                    }
                });
    }
}
