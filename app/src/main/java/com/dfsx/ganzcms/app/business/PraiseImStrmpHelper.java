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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyang on 2017/7.13
 */
public class PraiseImStrmpHelper implements IGetPraistmp {
    public static final String TAG = "PraiseImStrmpHelper";
    public static final String Account = "1000";


    String dir = Environment.getExternalStorageDirectory() + File.separator + "data/cachecms/ini/";
    String newsName = "prise-news.ini";
    String communityName = "prise-communtiy.ini";

    private PraistmpType eType;

    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

//    Map<Long, PriseModel> dNewslist;
//    Map<Long, PriseModel> dCommnuncitylist;

    Map<Long, PriseModel> dList;
    String path = "";
    String filename = "";

    /**
     * 保证只有一个CrashHandler实例
     */
    public PraiseImStrmpHelper(PraistmpType type) {
        this.eType = type;
//        path = FileUtil.getFileByAccountId(App.getInstance().getApplicationContext(),filename,"10000");
        if (eType == PraistmpType.PRISE_NEWS) {
            filename = newsName;
        } else {
            filename = communityName;
        }
//        path += filename;
        new Thread() {
            @Override
            public void run() {
                initData();
            }
        }.start();
    }

    public void initData() {
        try {
//            dList = (Map<Long, PriseModel>) FileUtil.getFile(path);
            dList = (Map<Long, PriseModel>) FileUtil.getFileByAccountId(App.getInstance().getApplicationContext(), filename, Account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Long, PriseModel> getDList() {
        return dList;
    }

    public void saveListToFile() {
        new Thread() {
            @Override
            public void run() {
//                FileUtil.saveFile(dir, filename, dList);
                FileUtil.saveFileByAccountId(App.getInstance().getApplicationContext(), filename, Account, dList);
            }
        }.start();
    }

    public void updateValuse(long id, boolean isPrise, boolean isStrmp, boolean isRead) {
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null) {
                if (isPrise)
                    model.setIsPraise(isPrise);
                if (isStrmp)
                    model.setIsStrmp(isStrmp);
                if (isRead)
                    model.setIsRead(isRead);
            } else {
                model = new PriseModel(id, isPrise, isStrmp, isRead);
            }
            dList.put(id, model);
        } else {
            if (dList == null) dList = new HashMap<>();
            PriseModel mode = new PriseModel(id, isPrise, isStrmp, isRead);
            dList.put(id, mode);
        }
        saveListToFile();
    }

    public void updateAttion(long id, int isAttionfalg) {
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null) {
                model.setAttion(isAttionfalg);
//            dList.put(id, model);
            } else {
                if (dList == null) dList = new HashMap<>();
                PriseModel mode = new PriseModel(id, isAttionfalg, false);
                dList.put(id, mode);
            }
        }
    }

    public void updateFav(long id, boolean isFavfalg) {
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null) {
                model.setFav(isFavfalg);
//            dList.put(id, model);
            } else {
                if (dList == null) dList = new HashMap<>();
                PriseModel mode = new PriseModel(id, 0, isFavfalg);
                dList.put(id, mode);
            }
        }
    }

    @Override
    public boolean isPriseFlag(long id) {
        boolean flag = false;
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null && model.isPraise()) flag = true;
        }
        return flag;
    }

    @Override
    public boolean isStrmpFlag(long id) {
        boolean flag = false;
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null && model.isStrmp()) flag = true;
        }
        return flag;
    }

    @Override
    public boolean isRead(long id) {
        boolean flag = false;
        if (dList != null && dList.size() > 0) {
            PriseModel model = dList.get(id);
            if (model != null && model.isRead()) flag = true;
        }
        return flag;
    }

    @Override
    public int isAttionUuser(long id, int role) {
        //0:没关注  1：关注
        int result = 0;
        if (!(dList == null || dList.isEmpty())) {
            PriseModel model = dList.get(id);
            if (model != null) {
                result = model.isAttion();
            }
        } else {
            checkList();
            PriseModel model = new PriseModel(id, role, false);
            model.setAttion(role);
            result = role;
            dList.put(id, model);
        }
        return result;
    }

    public void updateAttionUuser(long id, int role) {
        //0:没关注  1：关注
        if (!(dList == null || dList.isEmpty())) {
            PriseModel model = dList.get(id);
            if (model != null) {
                int blag = role == 0 ? 1 : 0;
                model.setAttion(blag);
                int a = 0;
            }
        }
    }

    @Override
    public void updateValuse(long id, int role, boolean isfavr) {

    }

    public void checkList() {
        if (dList == null)
            dList = new HashMap<>();
    }

    @Override
    public boolean isFavority(long communityId) {
        return false;
    }


}
