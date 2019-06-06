package com.dfsx.lzcms.liveroom.util;

import android.text.TextUtils;
import com.dfsx.lzcms.liveroom.R;

import java.util.HashMap;

/**
 * 用户的心形颜色值
 * Created by liuwb on 2016/11/10.
 */
public class HeartColorHelper {

    private static HeartColorHelper instance = new HeartColorHelper();

    private HashMap<String, String> resMap = new HashMap<>();

    private int[] resArr = new int[]{
            R.drawable.flaot_heart, R.drawable.flaot_star,
            R.drawable.float_flower, R.drawable.float_bone,
            R.drawable.float_fish
    };

    private HeartColorHelper() {

    }

    public static HeartColorHelper getInstance() {
        return instance;
    }

    public String getColor(String userName) {
        String name = StringUtil.getRoomMemberName(userName);
        if (resMap.get(name) != null) {
            return resMap.get(name);
        }
        String c = createColor();
        resMap.put(name, c);
        return c;
    }

    public int getFloatResId(String userName) {
        int res = 0;
        String name = StringUtil.getRoomMemberName(userName);
        if (resMap.get(name) != null) {
            if (TextUtils.isDigitsOnly(resMap.get(name))) {
                res = Integer.valueOf(resMap.get(name));
            }
        }
        if (res != 0) {
            return res;
        }

        res = getResIdRandom();
        resMap.put(name, res + "");

        return res;
    }

    public int getResIdRandom() {
        int count = (int) (Math.random() * 5);
        return resArr[count];
    }

    /**
     * 随机取一个颜色值
     *
     * @return
     */
    public String getColorRandom() {
        return createColor();
    }

    private String createColor() {
        int r = (int) (0 + Math.random() * (255 - 0 + 1));
        int g = (int) (0 + Math.random() * (255 - 0 + 1));
        int b = (int) (0 + Math.random() * (255 - 0 + 1));
        String red = Integer.toHexString(r);
        String green = Integer.toHexString(g);
        String blue = Integer.toHexString(r);
        return "#" + parseColor(red) + parseColor(green) + parseColor(blue);
    }

    private String parseColor(String s) {
        return s.length() < 2 ? "0" + s : s;
    }
}
