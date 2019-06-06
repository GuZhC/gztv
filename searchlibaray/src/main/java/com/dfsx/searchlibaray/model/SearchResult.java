package com.dfsx.searchlibaray.model;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable {
    private long total;
    private List<SearchItemInfo> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<SearchItemInfo> getData() {
        return data;
    }

    public void setData(List<SearchItemInfo> data) {
        this.data = data;
    }
}
