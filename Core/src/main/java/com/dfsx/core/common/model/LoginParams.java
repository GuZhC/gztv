package com.dfsx.core.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/9/2.
 */
public class LoginParams implements Serializable, Parcelable {

    public static final int LOGIN_TYPE_USER = 1001;
    public static final int LOGIN_TYPE_THIRD_USER = 1002;

    public int loginType;
    public String userName;
    public String password;
    public String provider;
    public String client_id;
    public String access_token;
    public String uid;

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(loginType);
        out.writeString(userName);
        out.writeString(password);
        out.writeString(provider);
        out.writeString(client_id);
        out.writeString(access_token);
        out.writeString(uid);

    }

    public static final Creator<LoginParams> CREATOR = new Creator<LoginParams>() {
        @Override
        public LoginParams createFromParcel(Parcel in) {
            LoginParams account = new LoginParams();
            account.loginType = in.readInt();
            account.userName = in.readString();
            account.password = in.readString();
            account.provider = in.readString();
            account.client_id = in.readString();
            account.access_token = in.readString();
            account.uid = in.readString();
            return account;
        }

        @Override
        public LoginParams[] newArray(int size) {
            return new LoginParams[size];
        }
    };
}
