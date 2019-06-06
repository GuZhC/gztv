package com.dfsx.core.common.Util;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.file.FileUtil;

/**
 * Created by liuwb on 2016/10/31.
 */
public class LoginCacheFileUtil {

    public static final String ACCOUNT_DIR = "luzhoulogindata";
    public static final String ACCOUNT_FILE_NAME = "account_info.txt";


    public static String getAccountDir() {
        return ACCOUNT_DIR;
    }

    public static String getAccountFileName() {
        return ACCOUNT_FILE_NAME;
    }

    /**
     * 从缓存里面读取登录信息
     *
     * @return
     */
    public static Account getAccountFromCache() {
        Account account = (Account) FileUtil.getFileByAccountId(CoreApp.getInstance().getApplicationContext(),
                ACCOUNT_FILE_NAME, ACCOUNT_DIR);

        return account;
    }
}
