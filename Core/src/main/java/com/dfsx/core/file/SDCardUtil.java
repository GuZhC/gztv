package com.dfsx.core.file;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

/**
 * SD卡相关的操作方法
 * Created by liuwb on 2016/5/31.
 */
public class SDCardUtil {
    /**
     * SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 存在并且容量大于10MB
     *
     * @return
     */
    public static boolean isSDCardExistAndNotFull() {
        return isSDCardExistAndNotFull(10);
    }

    /**
     * 存在并且容量大于指定MB
     *
     * @param minMB
     * @return
     */
    public static boolean isSDCardExistAndNotFull(long minMB) {
        if (!isSDCardExist()) {
            return false;
        }

        long leftMB = getSDFreeSize();
        return leftMB >= minMB;
    }

    /**
     * 获取SD卡可用空间 MB
     *
     * @return
     */
    public static long getSDFreeSize() {
        try {
            // 取得SD卡文件路径
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            // 获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            // 空闲的数据块的数量
            long freeBlocks = sf.getAvailableBlocks();
            // 返回SD卡空闲大小
            // return freeBlocks * blockSize; //单位Byte
            // return (freeBlocks * blockSize)/1024; //单位KB
            return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 显示SD卡不存在或空间不足的警告
     */
    public static void showSDCardUnavailableWarning() {
        Log.e("SDCardUtil", "SD卡不存在或空间不足");
    }
}
