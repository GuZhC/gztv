package com.dfsx.searchlibaray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.dfsx.searchlibaray.model.SearchItemInfo;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtil {

    public static void goSearch(Context context, String contentFragName) {
        goSearch(context, contentFragName, null);
    }

    public static void goSearch(Context context, String contentFragName, Bundle bundle) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SearchActivity.KEY_CUSTOM_SEARCH_CONTENT_FRAGMENT, contentFragName);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static String getShowHtmlString(String showText, String colorText, String color) {
        String htmltext = "<font color='" + color + "'>" + colorText
                + "</font>";
        if (!TextUtils.isEmpty(showText)) {
            return showText.replace(colorText, htmltext);
        }
        return showText;
    }

    public static String getShowHtmlString(String showText, String colorText) {
        return getShowHtmlString(showText, colorText, "#ed4040");
    }

    public static String getShowTitleHtmlString(String showText, SearchItemInfo itemInfo) {
        if (!TextUtils.isEmpty(showText) && itemInfo != null) {
            String text = showText;
            if (!TextUtils.isEmpty(itemInfo.getTitle())) {
                ArrayList<String> searchTextList = getEMTextArray(itemInfo.getTitle());
                if (searchTextList != null && !searchTextList.isEmpty()) {
                    for (String colorText : searchTextList) {
                        text = getShowHtmlString(text, colorText);
                    }
                }
            }
            if (!TextUtils.isEmpty(itemInfo.getBody())) {
                ArrayList<String> searchTextList = getEMTextArray(itemInfo.getBody());
                if (searchTextList != null && !searchTextList.isEmpty()) {
                    for (String colorText : searchTextList) {
                        text = getShowHtmlString(text, colorText);
                    }
                }
            }
            return text;
        }
        return showText;
    }

    public static ArrayList<String> getEMTextArray(String text) {
        String regx = "<em>(.*?)<\\/em>";
        Pattern pattern = Pattern.compile(regx);
        Matcher match = pattern.matcher(text);
        ArrayList<String> list = new ArrayList<>();
        while (match.find()) {
            list.add(match.group(1));
        }
        return list;
    }
}
