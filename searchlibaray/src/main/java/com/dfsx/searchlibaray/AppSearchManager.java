package com.dfsx.searchlibaray;

public class AppSearchManager {

    private static AppSearchManager instance = new AppSearchManager();

    private AppSearchManager() {

    }

    private ISearchConfig searchConfig;

    public static AppSearchManager getInstance() {
        return instance;
    }

    public ISearchConfig getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(ISearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }
}
