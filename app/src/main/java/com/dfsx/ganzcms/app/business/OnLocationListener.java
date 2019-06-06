package com.dfsx.ganzcms.app.business;

import com.baidu.location.BDLocation;

/**
 * Created by heyang on  2018/1/17.
 */
public interface OnLocationListener {

    public void onLocateSuccess(BDLocation location);

    public void onLocateFail(int errorType);
}
