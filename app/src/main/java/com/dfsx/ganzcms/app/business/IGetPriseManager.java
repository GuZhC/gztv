package com.dfsx.ganzcms.app.business;

import com.dfsx.ganzcms.app.model.PraistmpType;


/**
 * authpr  heyang 2017-4-12
 */

public class IGetPriseManager {
    private static IGetPriseManager mGetPriseManage = null;

    PraiseImStrmpHelper newsPriseHelper;
    PraiseImStrmpHelper communtiyPriseHelper;
    TopiclistHelper topiclistHelper;

    public static IGetPriseManager getInstance() {
        if (mGetPriseManage == null) {
            mGetPriseManage = new IGetPriseManager();
        }
        return mGetPriseManage;
    }

    public void init() {
        newsPriseHelper = new PraiseImStrmpHelper(PraistmpType.PRISE_NEWS);
        communtiyPriseHelper = new PraiseImStrmpHelper(PraistmpType.PRISE_COMMUNNITY);
        topiclistHelper = new TopiclistHelper(PraistmpType.PRISE_TOPIC);
    }

    public IGetPraistmp getProseToken(PraistmpType type) {
        IGetPraistmp temp = topiclistHelper;
        switch (type) {
            case PRISE_NEWS:
                temp = newsPriseHelper;
                break;
            case PRISE_COMMUNNITY:
                temp = communtiyPriseHelper;
                break;
        }
        return temp;
//        return type == PraistmpType.PRISE_NEWS ? newsPriseHelper : communtiyPriseHelper;
    }

}

