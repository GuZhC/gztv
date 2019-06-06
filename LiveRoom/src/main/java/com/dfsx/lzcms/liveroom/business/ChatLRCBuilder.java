package com.dfsx.lzcms.liveroom.business;

import android.util.Log;
import com.dfsx.lzcms.liveroom.model.LRCRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 准备的聊天文件解析器
 * Created by liuwb on 2016/12/14.
 */
public class ChatLRCBuilder implements ILRCBuilder {
    static final String TAG = "ChatLRCBuilder";

    @Override
    public List<LRCRow> getLRCRowList(String rawLrc) {
        Log.d(TAG, "getLrcRows by rawString");
        if (rawLrc == null || rawLrc.length() == 0) {
            Log.e(TAG, "getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LRCRow> rows = new ArrayList<>();
        try {
            //循环地读取歌词的每一行
            do {
                line = br.readLine();
                /**
                 一行歌词只有一个时间的  例如：徐佳莹   《我好想你》
                 [2016-12-23 14:24:45] <publish>
                 [2016-12-23 14:25:13] <message to="live56@conference.ds.net.cn" id="qE4ew-345" type="groupchat" from="ppp@ds.net.cn/Smack"><body>yugugjjbhjh</body></message>
                 **/
                Log.d(TAG, "lrc raw line: " + line);
                if (line != null && line.length() > 0) {
                    //解析每一行歌词 得到每行歌词的集合，因为有些歌词重复有多个时间，就可以解析出多个歌词行来
                    LRCRow lrcRow = LRCRow.createRows(line);
                    if (lrcRow != null) {
                        rows.add(lrcRow);
                    }
                }
            } while (line != null);

            if (rows.size() > 0) {
                // 根据歌词行的时间排序
                Collections.sort(rows);
                if (rows != null && rows.size() > 0) {
                    long startTime = rows.get(0).getTime();
                    for (LRCRow lrcRow : rows) {
                        lrcRow.setdTimeStamp(lrcRow.getTime() - startTime);
                        Log.d(TAG, "lrcRow:" + lrcRow.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "parse exceptioned:" + e.getMessage());
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }
}
