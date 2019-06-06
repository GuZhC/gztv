package com.loveplusplus.update;

import android.text.TextUtils;

/**
 * 2.0.38979
 */
public class AppVersion {

    private String versionName;
    private int versionCode;

    public AppVersion(String versionName, int code) {
        this.versionName = versionName;
        this.versionCode = code;
    }

    public boolean isNewerVersion(AppVersion version) {
        if (TextUtils.equals(versionName, version.getVersionName())) {
            return versionCode > version.getVersionCode();
        } else {
            if (!TextUtils.isEmpty(versionName) &&
                    !TextUtils.isEmpty(version.getVersionName())) {
                String[] names1 = versionName.split("\\.");
                String[] names2 = version.getVersionName().split("\\.");
                int count = Math.min(names1.length, names2.length);
                for (int i = 0; i < count; i++) {
                    try {
                        int v1 = Integer.valueOf(names1[i]);
                        int v2 = Integer.valueOf(names2[i]);
                        if (v1 > v2) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return versionCode > version.getVersionCode();
            } else {
                return versionCode > version.getVersionCode();
            }
        }
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
