package com.dfsx.core.network;

import java.util.HashMap;

/**
 * edited by yanyi on 15/8/24.
 */
public interface IHttpParameters {
    /**
     * 返回JSON格式的String
     * @return
     */
    String fromJSON();

    String fromGet();

    HashMap<String, String> toMap();

    IHttpParameters setTag(Object tag);

    Object getTag();

    HashMap<String, String> getHeader();
}
