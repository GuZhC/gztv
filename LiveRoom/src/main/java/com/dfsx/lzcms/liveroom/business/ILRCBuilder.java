package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.LRCRow;

import java.util.List;

/**
 * Created by liuwb on 2016/12/14.
 */
public interface ILRCBuilder {

    List<LRCRow> getLRCRowList(String lrcString);
}
