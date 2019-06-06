package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.core.file.FileUtil;
import com.dfsx.ganzcms.app.model.PraistmpType;
import com.dfsx.ganzcms.app.model.PriseModel;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heyang on 2017/7.13
 */
public class TopiclistHelper implements IGetPraistmp {
    public static final String filename = "att-communtiy.ini";
    public static final String TAG = "TopiclistHelper";
    public static final String Account = "1100";
    String communityName = "att-communtiy.ini";

    private PraistmpType eType;

    //程序的Context对象
    private Context mContext;

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    Map<Long, TopicalEntry> dList;

    /**
     * 保证只有一个CrashHandler实例
     */
    public TopiclistHelper(PraistmpType type) {
        new Thread() {
            @Override
            public void run() {
                initData();
            }
        }.start();
    }

    public void initData() {
        try {
            dList = (Map<Long, TopicalEntry>) FileUtil.getFileByAccountId(App.getInstance().getApplicationContext(), filename, Account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Long, TopicalEntry> getDllList() {
        return dList;
    }

    public List<TopicalEntry> getTopicList() {
        List<TopicalEntry> arr = null;
        if (!(dList == null || dList.isEmpty())) {
            Collection values = dList.values();
            for (Object object : values) {
                arr.add((TopicalEntry) object);
            }
//                        arr = (List<TopicalEntry>) dList.values();
        }
        return arr;
    }

    @Override
    public Map<Long, PriseModel> getDList() {
        return null;
    }

    public void saveListToFile() {
        new Thread() {
            @Override
            public void run() {
                FileUtil.saveFileByAccountId(App.getInstance().getApplicationContext(), filename, Account, dList);
            }
        }.start();
    }

    @Override
    public void updateValuse(long id, boolean isPrise, boolean isStrmp, boolean isRead) {

    }

    @Override
    public boolean isPriseFlag(long id) {
        return false;
    }

    @Override
    public boolean isStrmpFlag(long id) {
        return false;
    }

    @Override
    public boolean isRead(long id) {
        return false;
    }

    @Override
    public int isAttionUuser(long userId, int role) {
        return 0;
    }

    @Override
    public boolean isFavority(long communityId) {
        return false;
    }

    @Override
    public void updateAttionUuser(long userId, int role) {

    }

    public void updateValuse(long id, int role, boolean isfavr) {
        if (dList != null && dList.size() > 0) {
            TopicalEntry model = dList.get(id);
            if (model != null) {
                model.setRelationRole(role);
                model.setIsFavl(isfavr);
            }
        }
//        saveListToFile();
    }

    public void setDatelist(List<TopicalEntry> list) {
        checkList();
        dList.clear();
        if (!(list == null || list.isEmpty())) {
            for (TopicalEntry topicalEntry : list)
                dList.put(topicalEntry.getId(), topicalEntry);
        }
        saveListToFile();
    }

    public void checkList() {
        if (dList == null)
            dList = new HashMap<>();
    }


}
