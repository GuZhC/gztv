package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by heyang on  2018/4/19
 */
public class ColumnHelperManager extends DataFileCacheManager<ArrayList<ColumnCmsEntry>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER   ctrL+shoft+U
     */
    public static final String COLUMNHELPERMANAGER = App.getInstance().getPackageName() + "ColumnHelperManager.tx";
    public static final String COLUMNCODE = "column";
    private String _columnCode;
    private long columnId;

    public ColumnHelperManager(Context context) {
        super(context, COLUMNHELPERMANAGER + "_" + COLUMNCODE);
    }

    public void getAllColumns() {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/v2/columns";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), false);
    }

    public void getAllColumns(boolean isReadCache) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/v2/columns";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), false, isReadCache);
    }

    public String getjsonKey(JSONObject json,ColumnCmsEntry entry) throws JSONException {
        String key = json.optString("key");
        JSONObject filed = json.optJSONObject("fields");
        if (filed != null) {
            if (filed.has("category")) {
                key = filed.optString("category");
                if (key == null || TextUtils.isEmpty(key)) {
                    JSONArray arrs = filed.optJSONArray("category");
                    if (arrs != null && arrs.length() > 0)
                        key = arrs.get(0).toString();
                }
            }

            if (filed.has("list_type")) {
                //  针对bianmin   显示不同的样式  list:九宫格  bianming:子栏目
                String values = filed.optString("list_type");
                entry.setList_type(values);
            }

            if (TextUtils.equals(key, ColumnSpecialType.Yingji_type)){
                ColumnBasicListManager.getInstance().setYingjiColumn(entry);
            }

        }
        return key;
    }

    /**
     * @Override public ArrayList<ColumnCmsEntry> jsonToBean(JSONObject json) {
     * ArrayList<ColumnCmsEntry> dlist = null;
     * Map<Long, ColumnCmsEntry> lists = null;
     * Map<String, ColumnCmsEntry> unAssigend = null;
     * if (json != null && !TextUtils.isEmpty(json.toString())) {
     * JSONArray arr = json.optJSONArray("result");
     * if (arr != null) {
     * lists = new LinkedHashMap<Long, ColumnCmsEntry>();
     * dlist = new ArrayList<ColumnCmsEntry>();
     * try {
     * for (int k = 0; k < arr.length(); k++) {
     * JSONObject object = (JSONObject) arr.get(k);
     * ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
     * String key = object.optString("key");
     * entry.setMachine_code(key);
     * entry.setKey(getjsonKey(object));
     * if (entry.getParent_id() == -1 || entry.getParent_id() == 0)
     * lists.put(entry.getId(), entry);
     * }
     * for (int p = 0; p < arr.length(); p++) {
     * JSONObject object = (JSONObject) arr.get(p);
     * ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
     * columnId = entry.getId();
     * String key = object.optString("key");
     * entry.setMachine_code(key);
     * entry.setKey(getjsonKey(object));
     * if (entry != null && entry.getParent_id() != 0) {
     * ColumnCmsEntry parent = lists.get(entry.getParent_id());
     * if (parent != null) {
     * if (entry.getKey() != null && TextUtils.equals(entry.getKey(), "slider")) {
     * parent.setSliderId(entry.getId());
     * }
     * if (entry.getKey() != null && (TextUtils.equals(entry.getKey(), "quick-entry")
     * || TextUtils.equals(entry.getName(), "手机动态入口"))) {
     * parent.setDynamicId(entry.getId());
     * } else {
     * List<ColumnCmsEntry> rpss = parent.getDlist();
     * if (rpss == null) rpss = new ArrayList<>();
     * rpss.add(entry);
     * parent.setDlist(rpss);
     * }
     * }
     * }
     * }
     * for (int i = 0; i < arr.length(); i++) {
     * JSONObject object = (JSONObject) arr.get(i);
     * ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
     * columnId = entry.getId();
     * String key = object.optString("key");
     * entry.setMachine_code(key);
     * entry.setKey(getjsonKey(object));
     * if (entry != null && entry.getParent_id() != 0) {
     * ColumnCmsEntry parent = lists.get(entry.getParent_id());
     * if (parent != null) {
     * //                                            if (entry.getKey() != null && TextUtils.equals(entry.getKey(), "slider")) {
     * //                                                parent.setSliderId(entry.getId());
     * //                                            }
     * //                                            if (entry.getKey() != null && TextUtils.equals(entry.getKey(), "quick-entry")) {
     * //                                                parent.setDynamicId(entry.getId());
     * //                                            } else {
     * //                                                LinkedHashSet<ColumnCmsEntry> rpss = parent.getDlist();
     * //                                                if (rpss == null) rpss = new LinkedHashSet<ColumnCmsEntry>();
     * //                                                rpss.add(entry);
     * //                                                parent.setDlist(rpss);
     * //                                            }
     * } else {
     * for (Long obj : lists.keySet()) {
     * ColumnCmsEntry value = lists.get(obj);
     * List<ColumnCmsEntry> vicParent = value.getDlist();
     * if (vicParent != null && vicParent.size() > 0) {
     * for (ColumnCmsEntry child : vicParent) {
     * if (child == null) continue;
     * if (child.getId() == entry.getParent_id()) {
     * List<ColumnCmsEntry> rpss = child.getDlist();
     * //子栏目获取幻灯片
     * if (TextUtils.equals(entry.getKey(), "slider")) {
     * child.setSliderId(entry.getId());
     * }
     * if (TextUtils.equals(entry.getKey(), "quick-entry")
     * || TextUtils.equals(entry.getName(), "手机动态入口")) {
     * child.setDynamicId(entry.getId());
     * }
     * if (rpss == null) {
     * rpss = new ArrayList<>();
     * rpss.add(entry);
     * child.setDlist(rpss);
     * } else {
     * if (!rpss.isEmpty()) {
     * boolean isflag = false;
     * for (ColumnCmsEntry op : rpss) {
     * if (op.getId() == entry.getId()) {
     * isflag = true;
     * break;
     * }
     * }
     * if (!isflag) {
     * rpss.add(entry);
     * child.setDlist(rpss);
     * }
     * }
     * }
     * break;
     * }
     * if (!(child.getDlist() == null || child.getDlist().isEmpty())) {
     * for (ColumnCmsEntry lp : child.getDlist()) {
     * if (lp.getId() == entry.getParent_id()) {
     * List<ColumnCmsEntry> rpss = lp.getDlist();
     * if (rpss == null) {
     * rpss = new ArrayList<>();
     * rpss.add(entry);
     * lp.setDlist(rpss);
     * } else {
     * if (!rpss.isEmpty()) {
     * boolean isflag = false;
     * for (ColumnCmsEntry op : rpss) {
     * if (op.getId() == entry.getId()) {
     * isflag = true;
     * break;
     * }
     * }
     * if (!isflag) {
     * rpss.add(entry);
     * lp.setDlist(rpss);
     * }
     * }
     * }
     * break;
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * <p>
     * for (int i = 0; i < arr.length(); i++) {
     * JSONObject object = (JSONObject) arr.get(i);
     * ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
     * String key = object.optString("key");
     * entry.setMachine_code(key);
     * entry.setKey(getjsonKey(object));
     * columnId = entry.getId();
     * if (entry != null && entry.getParent_id() != 0) {
     * ColumnCmsEntry parent = lists.get(entry.getParent_id());
     * if (parent != null) {
     * //                                            if (entry.getKey() != null && TextUtils.equals(entry.getKey(), "slider")) {
     * //                                                parent.setSliderId(entry.getId());
     * //                                            }
     * //                                            if (entry.getKey() != null && TextUtils.equals(entry.getKey(), "quick-entry")) {
     * //                                                parent.setDynamicId(entry.getId());
     * //                                            } else {
     * //                                                LinkedHashSet<ColumnCmsEntry> rpss = parent.getDlist();
     * //                                                if (rpss == null) rpss = new LinkedHashSet<ColumnCmsEntry>();
     * //                                                rpss.add(entry);
     * //                                                parent.setDlist(rpss);
     * //                                            }
     * } else {
     * for (Long obj : lists.keySet()) {
     * ColumnCmsEntry value = lists.get(obj);
     * List<ColumnCmsEntry> vicParent = value.getDlist();
     * if (vicParent != null && vicParent.size() > 0) {
     * for (ColumnCmsEntry child : vicParent) {
     * if (child == null) continue;
     * if (child.getId() == entry.getParent_id()) {
     * List<ColumnCmsEntry> rpss = child.getDlist();
     * //子栏目获取幻灯片
     * if (TextUtils.equals(entry.getKey(), "slider")) {
     * child.setSliderId(entry.getId());
     * }
     * if (TextUtils.equals(entry.getKey(), "quick-entry")
     * || TextUtils.equals(entry.getName(), "手机动态入口")) {
     * child.setDynamicId(entry.getId());
     * }
     * if (rpss == null) {
     * rpss = new ArrayList<>();
     * rpss.add(entry);
     * child.setDlist(rpss);
     * } else {
     * if (!rpss.isEmpty()) {
     * boolean isflag = false;
     * for (ColumnCmsEntry op : rpss) {
     * if (op.getId() == entry.getId()) {
     * isflag = true;
     * break;
     * }
     * }
     * if (!isflag) {
     * rpss.add(entry);
     * child.setDlist(rpss);
     * }
     * }
     * }
     * break;
     * }
     * if (!(child.getDlist() == null || child.getDlist().isEmpty())) {
     * for (ColumnCmsEntry lp : child.getDlist()) {
     * if (lp.getId() == entry.getParent_id()) {
     * List<ColumnCmsEntry> rpss = lp.getDlist();
     * if (rpss == null) {
     * rpss = new ArrayList<>();
     * rpss.add(entry);
     * lp.setDlist(rpss);
     * } else {
     * if (!rpss.isEmpty()) {
     * boolean isflag = false;
     * for (ColumnCmsEntry op : rpss) {
     * if (op.getId() == entry.getId()) {
     * isflag = true;
     * break;
     * }
     * }
     * if (!isflag) {
     * rpss.add(entry);
     * lp.setDlist(rpss);
     * }
     * }
     * }
     * break;
     * }
     * //                                                    else {
     * //                                                        if (!(lp.getDlist() == null || lp.getDlist().isEmpty())) {
     * //                                                            for (ColumnCmsEntry lpp : lp.getDlist()) {
     * //                                                                if (lpp.getId() == entry.getParent_id()) {
     * //                                                                    List<ColumnCmsEntry> rpss = lpp.getDlist();
     * //                                                                    if (rpss == null)
     * //                                                                        rpss = new ArrayList<>();
     * //                                                                    rpss.add(entry);
     * //                                                                    lpp.setDlist(rpss);
     * //                                                                    break;
     * //                                                                }
     * //                                                            }
     * //                                                        }
     * //                                                    }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * int count = lists.size();
     * int bb = 0;
     * } catch (JSONException e) {
     * e.printStackTrace();
     * }
     * if (lists != null && !lists.isEmpty()) {
     * if (unAssigend == null) unAssigend = new LinkedHashMap<>();
     * for (Long obj : lists.keySet()) {
     * ColumnCmsEntry value = lists.get(obj);
     * dlist.add(value);
     * unAssigend.put(value.getMachine_code(), value);
     * if (!(value.getDlist() == null || value.getDlist().isEmpty())) {
     * for (ColumnCmsEntry item : value.getDlist()) {
     * if (!(item.getDlist() == null || item.getDlist().isEmpty())) {
     * for (ColumnCmsEntry third : item.getDlist()) {
     * unAssigend.put(third.getMachine_code(), third);
     * }
     * }
     * unAssigend.put(item.getMachine_code(), item);
     * }
     * }
     * }
     * lists.clear();
     * }
     * if (unAssigend != null && unAssigend.size() > 0)
     * ColumnBasicListManager.getInstance().setUnAssigedMap(unAssigend);
     * }
     * }
     * return dlist;
     * }
     **/


    public void getDlist(ColumnCmsEntry parent, ColumnCmsEntry child) {
        List<ColumnCmsEntry> dlist = parent.getDlist();
        if (dlist == null) {
            dlist = new ArrayList<>();
            dlist.add(child);
            parent.setDlist(dlist);
        } else {
            if (!dlist.isEmpty()) {
                boolean isExist = false;
                for (ColumnCmsEntry op : dlist) {
                    if (op.getId() == child.getId()) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    dlist.add(child);
                    parent.setDlist(dlist);
                }
            }
        }
    }

    @Override
    public ArrayList<ColumnCmsEntry> jsonToBean(JSONObject json) {
        ArrayList<ColumnCmsEntry> dlist = null;
        Map<Long, ColumnCmsEntry> lists = null;
        Map<String, ColumnCmsEntry> unAssigend = null;
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            JSONArray arr = json.optJSONArray("result");
            if (arr != null) {
                lists = new LinkedHashMap<Long, ColumnCmsEntry>();
                dlist = new ArrayList<ColumnCmsEntry>();
                try {
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject object = (JSONObject) arr.get(k);
                        ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
                        String key = object.optString("key");
                        entry.setMachine_code(key);
                        entry.setKey(getjsonKey(object,entry));
                        if (entry.getParent_id() == -1 || entry.getParent_id() == 0)
                            lists.put(entry.getId(), entry);
                    }
                    int count = 0;
                    do {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject object = (JSONObject) arr.get(i);
                            ColumnCmsEntry entry = new Gson().fromJson(object.toString(), ColumnCmsEntry.class);
                            columnId = entry.getId();
                            String key = object.optString("key");
                            entry.setMachine_code(key);
                            entry.setKey(getjsonKey(object,entry));
                            if (entry != null && entry.getParent_id() != 0) {
                                ColumnCmsEntry parent = lists.get(entry.getParent_id());
                                if (parent != null) {
                                    if (TextUtils.equals(entry.getKey(), "slider")) {
                                        parent.setSliderId(entry.getId());
                                    }
                                    if ((TextUtils.equals(entry.getKey(), "quick-entry")
                                            || TextUtils.equals(entry.getName(), "手机动态入口"))) {
                                        parent.setDynamicId(entry.getId());
                                    } else {
                                        getDlist(parent, entry);
                                    }
                                } else {
                                    for (Long obj : lists.keySet()) {
                                        ColumnCmsEntry value = lists.get(obj);
                                        List<ColumnCmsEntry> vicParent = value.getDlist();
                                        if (vicParent != null && vicParent.size() > 0) {
                                            for (ColumnCmsEntry child : vicParent) {
                                                if (child == null) continue;
                                                if (child.getId() == entry.getParent_id()) {
                                                    List<ColumnCmsEntry> rpss = child.getDlist();
                                                    //子栏目获取幻灯片
                                                    if (TextUtils.equals(entry.getKey(), "slider")) {
                                                        child.setSliderId(entry.getId());
                                                    }
                                                    if (TextUtils.equals(entry.getKey(), "quick-entry")
                                                            || TextUtils.equals(entry.getName(), "手机动态入口")) {
                                                        child.setDynamicId(entry.getId());
                                                    }
                                                    getDlist(child, entry);
                                                    break;
                                                }
                                                if (!(child.getDlist() == null || child.getDlist().isEmpty())) {
                                                    for (ColumnCmsEntry lp : child.getDlist()) {
                                                        if (lp.getId() == entry.getParent_id()) {
                                                            getDlist(lp, entry);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        count++;
                    } while (count < 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (lists != null && !lists.isEmpty()) {
                    if (unAssigend == null) unAssigend = new LinkedHashMap<>();
                    for (Long obj : lists.keySet()) {
                        ColumnCmsEntry value = lists.get(obj);
                        dlist.add(value);
                        unAssigend.put(value.getMachine_code(), value);
                        if (!(value.getDlist() == null || value.getDlist().isEmpty())) {
                            for (ColumnCmsEntry item : value.getDlist()) {
                                if (!(item.getDlist() == null || item.getDlist().isEmpty())) {
                                    for (ColumnCmsEntry third : item.getDlist()) {
                                        unAssigend.put(third.getMachine_code(), third);
                                    }
                                }
                                unAssigend.put(item.getMachine_code(), item);
                            }
                        }
                    }
                    lists.clear();
                }
                if (unAssigend != null && unAssigend.size() > 0)
                    ColumnBasicListManager.getInstance().setUnAssigedMap(unAssigend);
            }
        }
        return dlist;
    }
}
