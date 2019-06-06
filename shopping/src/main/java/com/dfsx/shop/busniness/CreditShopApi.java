package com.dfsx.shop.busniness;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.CoreApp;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.shop.model.ExchangeShop;
import com.dfsx.shop.model.OrderInfo;
import com.dfsx.shop.model.ShopEntryInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * create by  heyang   2017-11-16
 * shop
 */

public class CreditShopApi {
    private DataRequest.DataCallback callback;
    private Context mContext;
    public String severUrl = "";
    public String pictureUrl = "";
    public String videoUrl = "";
    private Context context;

    public CreditShopApi(Context context) {
        mContext = context;
        severUrl = CoreApp.getInstance().getShoppServerUrl();
//        pictureUrl = App.getInstance().getmSession().getCommunityServerUrl() + "/public/pictures/uploader";
//        videoUrl = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/videos/uploader";
        this.context = context;
    }

    /**
     * 获取商品详情
     *
     * @param shopId
     * @param callback
     */
    public void getShopInfo(long shopId, DataRequest.DataCallback<ShopEntryInfo> callback) {
        String url = severUrl + "/public/commodities/" + shopId;
        new DataRequest<ShopEntryInfo>(context) {
            @Override
            public ShopEntryInfo jsonToBean(JSONObject json) {
                ShopEntryInfo info = null;
                if (json != null) {
                    try {
                        info = new Gson().
                                fromJson(json.toString(), ShopEntryInfo.class);
                        JSONArray imageArr = json.optJSONArray("images");
                        if (imageArr != null && imageArr.length() > 0) {
                            ArrayList<ShopEntryInfo.ImagesBean> imaglist = new ArrayList<ShopEntryInfo.ImagesBean>();
                            Gson g = new Gson();
                            for (int i = 0; i < imageArr.length(); i++) {
                                JSONObject obj = (JSONObject) imageArr.get(i);
                                if (obj != null) {
                                    ShopEntryInfo.ImagesBean beans = g.fromJson(obj.toString(), ShopEntryInfo.ImagesBean.class);
                                    if (beans == null) continue;
                                    imaglist.add(beans);
                                }
                            }
                            info.setImages(imaglist);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(CoreApp.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);
    }

    /**
     * 兑换 商品
     */
    public void exchangeShop(long cid, ExchangeShop shop, DataRequest.DataCallback callback) {
        String url = severUrl + "/public/commodities/" + cid + "/exchange";
        try {
            JSONObject obj = new JSONObject();
            obj.put("count", shop.getCount());
            JSONObject contact = new JSONObject();
            contact.put("id", shop.getContact().getId());
            contact.put("name", shop.getContact().getName());
            contact.put("phone_number", shop.getContact().getPhone_number());
            contact.put("address", shop.getContact().getAddress());
            obj.put("contact", contact);
            new DataRequest<String>(mContext) {
                @Override
                public String jsonToBean(JSONObject json) {
                    String code = "";
                    if (json != null && !TextUtils.isEmpty(json.toString())
                            && json.has("res")) {
                        code = json.optString("res");
                    }
                    return code;
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                            .setUrl(url).
                                    setRequestType(DataReuqestType.POST).
                                    setJsonParams(obj).
                                    setToken(CoreApp.getInstance().getCurrentToken()).
                                    build(),
                    false).setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看订单详情
     *
     * @param callback
     */
    public void getDefaultContact(DataRequest.DataCallback<ExchangeShop.ContactBean> callback) {
        String url = severUrl + "/public/users/current/contacts";
        new DataRequest<ExchangeShop.ContactBean>(context) {
            @Override
            public ExchangeShop.ContactBean jsonToBean(JSONObject json) {
                ExchangeShop.ContactBean info = null;
                if (json != null) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null && arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject item = (JSONObject) arr.get(i);
                                info = new Gson().
                                        fromJson(item.toString(), ExchangeShop.ContactBean.class);
                                if (info != null) {
                                    if (info.isLast_used())
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(CoreApp.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);
    }


    /**
     * 查看订单详情
     *
     * @param orderNumber
     * @param callback
     */
    public void getOrderInfo(String orderNumber, DataRequest.DataCallback<OrderInfo> callback) {
        String url = severUrl + "/public/orders/" + orderNumber;
        new DataRequest<OrderInfo>(context) {
            @Override
            public OrderInfo jsonToBean(JSONObject json) {
                OrderInfo info = null;
                if (json != null) {
                    try {
                        info = new Gson().
                                fromJson(json.toString(), OrderInfo.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(CoreApp.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);
    }


}
