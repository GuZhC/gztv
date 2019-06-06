package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.BetOption;
import com.dfsx.lzcms.liveroom.model.BetResponse;

/**
 * Created by liuwb on 2016/12/20.
 */
public interface Bet {

    void betTeam(BetOption team, double coins, ICallBack<BetResponse> callBack);
}
