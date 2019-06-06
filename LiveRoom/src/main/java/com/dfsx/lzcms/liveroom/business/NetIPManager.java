package com.dfsx.lzcms.liveroom.business;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

public class NetIPManager {
    public static Observable.OnSubscribe<String> getCurrentNetIp() {
        Observable.OnSubscribe<String> subscribe = new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String ip = getNetIp();
                subscriber.onNext(ip);
            }
        };
        return subscribe;
    }


    public static String getNetIp() {
        try {
            String sGetAddrUrl = "http://ip-api.com/json/";
            String res = HttpUtil.executeGet(sGetAddrUrl, new HttpParameters(), null);
            JSONObject jsonObject = new JSONObject(res);
            String ip = jsonObject.optString("query");
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
