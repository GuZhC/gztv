package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import android.util.Log;
import com.dfsx.lzcms.liveroom.model.LRCRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuwb on 2016/12/23.
 */
public class NetChatLRCBuilder implements ILRCBuilder {
    private String[] chatLRCSplitKeys = new String[]{
            "</message>"
    };

    @Override
    public List<LRCRow> getLRCRowList(String netUrl) {
        InputStreamReader inputreader = null;
        BufferedReader buffreader = null;
        InputStream is = null;
        try {
            URL url = new URL(netUrl);
            //打开连接
            URLConnection con = url.openConnection();
            //输入流
            is = con.getInputStream();
            if (is != null) {
                List<LRCRow> rows = new ArrayList<>();
                inputreader = new InputStreamReader(is);
                buffreader = new BufferedReader(inputreader);
                String line;
                String tempRowStr = "";

                //分行读取
                while ((line = buffreader.readLine()) != null) {
//                    Log.e("TAG", "line == " + line);
                    tempRowStr += line;
                    if (!TextUtils.isEmpty(tempRowStr)) {
                        String parserRowStr = "";
                        if (tempRowStr.endsWith("<publish>") ||
                                tempRowStr.endsWith("</publish>")) {
                            parserRowStr = tempRowStr;
                            tempRowStr = "";
                        } else {
                            String key = getRowKeyOfString(tempRowStr);
                            if (!TextUtils.isEmpty(key)) {
                                int index = tempRowStr.indexOf(key);
                                int end = index + key.length();
                                parserRowStr = tempRowStr.substring(0, end);
                                tempRowStr = tempRowStr.substring(end);
                            }
                        }
                        if (!TextUtils.isEmpty(parserRowStr)) {
                            LRCRow lrcRow = LRCRow.createRows(parserRowStr);
                            if (lrcRow != null) {
                                rows.add(lrcRow);
                            }
                        }
                    }
//                    if (!TextUtils.isEmpty(tempRowStr) &&
//                            isRowEndString(tempRowStr)) {
//                        LRCRow lrcRow = LRCRow.createRows(tempRowStr);
//                        if (lrcRow != null) {
//                            rows.add(lrcRow);
//                        }
//                        tempRowStr = "";
//                    }
                }


                if (rows.size() > 0) {
                    // 根据歌词行的时间排序
                    Collections.sort(rows);
                    if (rows != null && rows.size() > 0) {
                        long startTime = rows.get(0).getTime();
                        for (LRCRow lrcRow : rows) {
                            lrcRow.setdTimeStamp(lrcRow.getTime() - startTime);
//                            Log.e("TAG", "time == " + lrcRow.getdTimeStamp());
                        }
                    }
                }
                return rows;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (inputreader != null) {
                    inputreader.close();
                }
                if (buffreader != null) {
                    buffreader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean isRowEndString(String rowStr) {
        if (TextUtils.isEmpty(rowStr)) {
            return false;
        }
        if (rowStr.endsWith("<publish>") ||
                rowStr.endsWith("</message>") ||
                rowStr.contains("</message>")) {
            return true;
        }
        return false;
    }

    /**
     * 判断读取的字符串是不是以关键字结束的
     *
     * @param rowStr
     * @return
     */
    private boolean isEndKeyOfString(String rowStr) {
        if (TextUtils.isEmpty(rowStr)) {
            return false;
        }
        for (String key : chatLRCSplitKeys) {
            if (rowStr.endsWith(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否包含有关键字
     * 并返回关键字
     *
     * @param rowStr
     * @return
     */
    private String getRowKeyOfString(String rowStr) {
        if (TextUtils.isEmpty(rowStr)) {
            return null;
        }
        for (String key : chatLRCSplitKeys) {
            if (rowStr.contains(key)) {
                return key;
            }
        }
        return null;
    }
}
