package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/29.
 */
public interface TradeAction extends Serializable {

    long getTAId();

    /**
     * 交易虚拟币数
     *
     * @return
     */
    double getTACoins();

    /**
     * 交易的目标人物id
     *
     * @return
     */
    long getTATagPersonId();

    /**
     * 交易的目标人物用户名(账号名称)
     *
     * @return
     */
    String getTATagPersonName();

    /**
     * 交易的目标人物 的昵称
     *
     * @return
     */
    String getTATagPersonNickName();

    /**
     * 交易的目标人物logo
     *
     * @return
     */
    String getTATagPersonLogo();

    /**
     * 获取交易的事件
     *
     * @return 返回的是Linux 的timestamp
     */
    long getTATime();

    String getTADescribeText();
}
