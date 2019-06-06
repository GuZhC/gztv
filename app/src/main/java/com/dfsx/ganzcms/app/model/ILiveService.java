package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liuwb on 2017/7/13.
 */
public interface ILiveService {
    /**
     * 获取活动直播的ID
     *
     * @return
     */
    long getServiceId();

    /**
     * 获取活动直播的标题
     *
     * @return
     */
    String getServiceTitle();

    ArrayList<ILiveInputStream> getLiveInputStreamList();

    void setLiveInputStream(ArrayList<ILiveInputStream> inputStreamList);


    /**
     * 活动直播的输入流信息
     */
    public interface ILiveInputStream extends Serializable {
        long getInputId();

        String getInputName();

        String getInputRtmpUrl();

        boolean isSelected();

        void setSelected(boolean isSelected);
    }
}
