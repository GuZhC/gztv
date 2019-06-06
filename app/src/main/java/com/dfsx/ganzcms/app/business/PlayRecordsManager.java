package com.dfsx.ganzcms.app.business;

/**
 * Created by liuwb on 2016/9/2.
 */
public class PlayRecordsManager {

    private PlayRecords playRecords;

    private static PlayRecordsManager instance = new PlayRecordsManager();

    private PlayRecordsManager() {
        playRecords = new PlayRecords();
    }

    public static PlayRecordsManager getInstance() {
        return instance;
    }


    public PlayRecords getPlayRecords() {
        return playRecords;
    }

}
