package com.dfsx.searchlibaray.businness;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.CoreApp;
import com.dfsx.core.file.FileUtil;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class SearchHistoryManager {

    public static final int MAX_HIS_COUNT = 20;

    private static final String HISTORY_DIR = "all";
    private static final String HISTORY_FILE_NAME = "history.txt";

    public static ArrayList<String> getHistory() {
        Object ob = FileUtil.getFileByAccountId(CoreApp.getInstance().getApplicationContext(),
                HISTORY_FILE_NAME, HISTORY_DIR);
        if (ob != null && ob instanceof ArrayList) {
            return (ArrayList<String>) ob;
        }
        return null;
    }

    public static void addHistory(String text) {
        Observable.just(text)
                .observeOn(Schedulers.io())
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            ArrayList<String> list = getHistory();
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            if (!isExist(list, text)) {
                                if (list.size() >= MAX_HIS_COUNT) {
                                    list.remove(list.size() - 1);
                                }
                                list.add(0, text);
                                FileUtil.saveFileByAccountId(CoreApp.getInstance().getApplicationContext(),
                                        HISTORY_FILE_NAME, HISTORY_DIR, list);
                            }

                        }
                        return null;
                    }

                    private boolean isExist(ArrayList<String> list, String text) {
                        for (String s : list) {
                            boolean is = TextUtils.equals(s, text);
                            if (is) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .subscribe();

    }


    public static void clearHistory() {
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        FileUtil.delFileByAccontId(CoreApp.getInstance().getApplicationContext(),
                                HISTORY_FILE_NAME, HISTORY_DIR);
                        return null;
                    }
                }).subscribe();

    }
}
