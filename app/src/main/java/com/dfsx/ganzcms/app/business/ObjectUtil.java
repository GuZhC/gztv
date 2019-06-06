package com.dfsx.ganzcms.app.business;

public class ObjectUtil {

    public static int objectToInt(Object ob) {
        int num = 0;
        try {
            if (ob instanceof Double) {
                num = (int) ((double) ob);
            } else if (ob instanceof Integer) {
                num = (Integer) ob;
            } else {
                num = Integer.valueOf(ob.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
