package com.loveplusplus.update;

public interface IApkUpdateChecker {

    /**
     * Note: has run child thread
     *
     * @return
     */
    ApkCheckResult checkUpdate();
}
