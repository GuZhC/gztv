package com.loveplusplus.update;

public class ApkCheckResult {

    public boolean isNeedUpdate;

    public String apkDownloadUrl;

    public String updateMessage;

    public String updateVersion;

    public ApkCheckResult() {

    }

    public ApkCheckResult(boolean isUpdate, String url, String message,String  version) {
        this.isNeedUpdate = isUpdate;
        this.apkDownloadUrl = url;
        this.updateMessage = message;
        this.updateVersion=version;
    }
}
