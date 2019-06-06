package com.dfsx.ganzcms.app.push.PushMeesageModel;

import java.util.List;

/**
 * Created by wen on 2017/3/27.
 */

public class Messages {
    private long total;
    private List<Message> data;

    public Messages(long total, List<Message> data) {
        this.total = total;
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Message> getData() {
        return data;
    }

    public void setData(List<Message> data) {
        this.data = data;
    }
}
