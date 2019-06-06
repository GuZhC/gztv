package com.dfsx.core.network.datarequest;

/**
 * Created by liuwb on 2016/10/26.
 */
public class GetTokenManager {

    private static GetTokenManager instance = new GetTokenManager();

    private IGetToken tokenHelper;

    private GetTokenManager() {

    }

    public static GetTokenManager getInstance() {
        return instance;
    }

    public void setIGetToken(IGetToken iGetToken) {
        this.tokenHelper = iGetToken;
    }

    public IGetToken getIGetToken() {
        return tokenHelper;
    }
}
