package com.dfsx.ganzcms.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by liuwb on 2016/11/3.
 */
public class Test {

    public Test(String res) throws JSONException {
        JSONObject resJson = new JSONObject(res);
        Iterator iterator = resJson.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = resJson.getString(key);
            JSONObject siJson = new JSONObject(value);
        }
    }

    public ArrayList<AreaModel> bainliJson(String jsonObjStr) throws JSONException {
        JSONObject json = new JSONObject(jsonObjStr);
        Iterator iterator = json.keys();
        ArrayList<AreaModel> areaModels = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            AreaModel areaModel = new AreaModel(key);
            areaModels.add(areaModel);
            String value = json.getString(key);
            ArrayList<AreaModel> ams = null;
            if (value.startsWith("[")) {
                JSONArray array = new JSONArray(value);
                ams = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    ams.add(new AreaModel(array.optString(i)));
                }
            } else {
                ams = bainliJson(value);
            }
            areaModel.setChildArea(ams);
        }
        return areaModels;
    }
}
