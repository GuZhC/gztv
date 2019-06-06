package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import android.util.Log;
import com.dfsx.lzcms.liveroom.model.LRCRow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwb on 2017/1/10.
 */
public class Test {
    public static final String KEYWORDSTART = "<!—";
    public static final String KEYWORDEND = "->";

    public static void main(String[] args) {
        Gson g = new Gson();
        Map<String, Double> map = new HashMap<>();
        map.put("a", 11.5);
        map.put("b", 11.5);
        map.put("c", 11.5);
        String jsonStr = g.toJson(map);

        System.out.println(jsonStr);

        Map<String, Double> mapLL = g.fromJson(jsonStr, new TypeToken<Map<String, Double>>() {
        }.getType());

        System.out.println(mapLL.get("a") + "");
    }


    public String findReplaceString(String str) {
        if (str == null || str.length() == 0) {
            Log.e("TAG", "getLrcRows str null or empty");
            return null;
        }
        StringReader reader = new StringReader(str);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        String lineHeader = "";
        try {
            //循环地读取歌词的每一行
            do {
                line = br.readLine();
                System.out.println("line == " + line);
                //                Log.d("TAG", "str line: " + line);
                if (!"".equals(line)) {
                    String tempLine = lineHeader + line;
                    String[] tempArr = findAndReplace(tempLine);
                    stringBuffer.append(tempArr[0]);
                    lineHeader = tempArr[1];
                }
            } while (line != null);

            return stringBuffer.toString();
        } catch (Exception e) {
            //            Log.e("TAG", "parse exceptioned:" + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
    }

    public String[] findAndReplace(String lineStr) {
        String[] backStrArr = new String[2];
        String str = lineStr;
        String tempStr = "";
        while (isFind(str)) {
            int start = str.indexOf(KEYWORDSTART);
            int end = str.indexOf(KEYWORDEND, start);
            System.out.println("start == " + start);
            System.out.println("end == " + end);
            if (start < end) {
                String selectedStr = str.substring(start, end + KEYWORDEND.length());
                System.out.println("selectedStr == " + selectedStr);
                str = str.replace(selectedStr, getReplaceText());
            } else if (end == -1) {
                tempStr = str.substring(start, str.length());
                str = str.substring(0, start);
            }
        }
        System.out.println("back line == " + str);
        System.out.println("back temp == " + tempStr);
        backStrArr[0] = str == null ? "" : str;
        backStrArr[1] = tempStr;
        return backStrArr;
    }

    private boolean isFindSuitText(String lineStr) {
        if (lineStr.contains(KEYWORDSTART) &&
                lineStr.contains(KEYWORDEND)) {
            int startIndex = lineStr.indexOf(KEYWORDSTART);
            int end = lineStr.indexOf(KEYWORDEND, startIndex);
            System.out.println("startIndex == " + startIndex);
            System.out.println("end == " + end);
            return end > startIndex;
        }
        return false;
    }

    private boolean isFind(String lineStr) {
        return lineStr.contains(KEYWORDSTART);
    }

    public String getReplaceText() {

        return "2222222222------";
    }
}
