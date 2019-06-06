package com.dfsx.ganzcms.app.view.live;

import android.content.Context;
import android.location.Location;
import com.baidu.mapapi.*;

public class MapUtils {
    private BMapManager mapManager;
    private MKLocationManager locationManager;

    private MapCallBack mMapCallBack;

    public MapUtils(Context context, MapCallBack map) {
        this.mMapCallBack = map;

        mapManager = new BMapManager(context);
        locationManager = mapManager.getLocationManager();

        mapManager.init("53351EE4BDE7BD870F41A0B4AF1480F1CA97DAF9",
                new MyMKGeneralListener());
//
//        locationManager.setNotifyInternal(20, 5);
//
//
//        // 注册位置更新事件
//        locationManager.requestLocationUpdates(new MyLocationListener());
//
////        context.unregisterReceiver(new MyLocationListener());
//
//        mapManager.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                locationManager.setNotifyInternal(20, 5);
                // 注册位置更新事件
                locationManager.requestLocationUpdates(new MyLocationListener());
                mapManager.start();
            }
        }).start();
    }

    public void free() {
        if (mapManager != null) {
            mapManager.destroy();
        }
    }

    // 定位自己的位置，只定位一次
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location arg0) {
            // arg0  有时为空。报异常
            if (arg0 != null) {
                double jingdu1 = arg0.getLatitude();
                double weidu1 = arg0.getLongitude();

                int jingdu = (int) (arg0.getLatitude() * 1000000);
                int weidu = (int) (arg0.getLongitude() * 1000000);
//            tv1.setText("经度：" + jingdu1 + ",纬度：" + weidu1);
//            System.out.println("经度：" + jingdu1 + ",纬度：" + weidu1);
                MKSearch search = new MKSearch();
                search.init(mapManager, new MyMKSearchListener());
                search.reverseGeocode(new GeoPoint(jingdu, weidu));
            }
        }
    }

    class MyMKSearchListener implements MKSearchListener {
        @Override
        public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
            if (arg0 == null) {
//                tv2.setText("没有获取想要的位置");
            } else {
                GeoPoint point = arg0.geoPt;
                double longitude = (double) (point.getLongitudeE6() / 1000000.0);
                double latitude = (double) (point.getLatitudeE6() / 1000000.0);
                mMapCallBack.MyCallBackEvent(0, arg0.strAddr, latitude, longitude);
//                tv2.setText("地址：" + arg0.strAddr + ",坐标："
//                        + point.getLatitudeE6() + "," + point.getLongitudeE6());
            }
        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    class MyMKGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int arg0) {
            if (arg0 == MKEvent.ERROR_NETWORK_CONNECT) {
                mMapCallBack.MyCallBackEvent(0, "无法获取你的地理位置信息！", 0, 0);
//                Toast.makeText(BaiduMapLocation.this, "您的网络出错啦！",
//                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int arg0) {
            if (arg0 == MKEvent.ERROR_PERMISSION_DENIED) {
                mMapCallBack.MyCallBackEvent(0, "API KEY 错误，请检查！", 0, 0);
//                Toast.makeText(BaiduMapLocation.this, "API KEY 错误，请检查！",
//                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface  MapCallBack{
        public  void  MyCallBackEvent(int  i,String url,double latff,double lontff);
    }


}
