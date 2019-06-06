package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.fragment.*;

import java.util.HashMap;

/**
 * 图文直播的动态主见管理
 * Created by liuwb on 2017/7/6.
 */
public class LiveServicePluginManager {

    /**
     * 图文直播的动态组件对应关系，
     * image-text——Fragment的类名
     */
    public static HashMap<String, String> pluginFragmentNameMap = new HashMap<String, String>() {
        {
            put("image-text", LiveServiceInfoFragment.class.getName());
            put("intro", LiveServiceIntroduceFragment.class.getName());
            put("vote", VoteWebFragment.class.getName());
            put("questionnaire", QuestionnaireFragment.class.getName());
            put("sign-up", SignupWebFragment.class.getName());
            put("quiz", LiveGuessFragment.class.getName());
            //            put("chat", LiveServiceChatFragment.class.getName()); //聊天主键一定会有
            put("lottery", LotteryWebFragment.class.getName());
        }
    };

    public static HashMap<String, String> pluginNameMap = new HashMap<String, String>() {
        {
            put("image-text", "直播");
            put("intro", "介绍");
            put("vote", "投票");
            put("questionnaire", "问卷");
            put("sign-up", "报名");
            put("quiz", "竞猜");
            //            put("chat", "聊天");
            put("lottery", "抽奖");
        }
    };


    public static String getPluginClassName(String pluginTag) {
        return pluginFragmentNameMap.get(pluginTag);
    }

    public static String getPluginName(String pluginTag) {
        return pluginNameMap.get(pluginTag);
    }
}
