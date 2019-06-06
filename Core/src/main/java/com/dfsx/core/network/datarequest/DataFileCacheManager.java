package com.dfsx.core.network.datarequest;

import android.content.Context;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;

import java.io.Serializable;

/**
 * 获取数据自动缓存
 * Created by liuwb on 2016/5/31.
 */
public abstract class DataFileCacheManager<T extends Serializable> extends DataRequest<T> {
    /**
     */
    private String accountId;

    /**
     */
    private String fileName;

    private T data;

    /**
     * @param context
     */
    public DataFileCacheManager(Context context, String fileName) {
        this(context, "1", fileName);
    }

    public DataFileCacheManager(Context context, String accountId, String fileName) {
        super(context);
        this.accountId = accountId;
        this.fileName = fileName;
    }

    /**
     * @return 可能null
     */
    public T getFileCacheData() {
        return (T) FileUtil.getFileByAccountId(context, fileName, accountId);
    }

    @Override
    protected void dispatchNetData(T data, boolean isAppend) {
        if (!isAppend) {
            FileUtil.saveFileByAccountId(context, fileName, accountId, data);
        }
    }

    @Override
    protected void readCacheData(boolean isAppend) {
        if (!isAppend) {
            T data = getFileCacheData();
            if (data != null) {
                sendSuccessMsg(data, isAppend);
            }
        }
    }

    public void deleteCacheData() {
        FileUtil.delFileByAccontId(context, fileName, accountId);
    }

    public void deleteFolerCache() {
        FileUtil.delAllFile(FileUtil.getFileDicByAccountId(context, accountId));
    }

}
