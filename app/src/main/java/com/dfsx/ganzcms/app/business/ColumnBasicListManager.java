package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.ColumnEntry;
import com.dfsx.ganzcms.app.model.LiveEntity;
import com.dfsx.videoijkplayer.media.VideoVoiceManager;
import rx.functions.Action1;

import java.util.*;

/**
 * 缓存公共需要调用的
 * Created by heyang on 2017/11/1.
 */
public class ColumnBasicListManager {

    private static ColumnBasicListManager instance = new ColumnBasicListManager();

    public static ColumnBasicListManager getInstance() {
        return instance;
    }

    private ArrayList<ColumnCmsEntry> _columnList;  //首页栏目信息;

    public ArrayList<ColumnCmsEntry> get_columnList() {
        return _columnList;
    }

    //针对 圈子详情  点赞是不是需要刷新
    public boolean  isRershFlag=false;
    public long  cmyId;
    public boolean  isPraiseflag;

    public boolean isEnbaleVoice = false;   //是否静音

    //    private int _pre_weight;
//    private boolean _iSameWeight = true;
    private String[] _recommedKsys = {"直播", "点播", "主持人"};

    //  电视直播频道
    private ArrayList<LiveEntity.LiveChannel> liveTvChannelMap;
    private LiveEntity.LiveChannel selectliveTvChanne;

    /**
     * 是否显示投稿的控件
     */
    private boolean isShowEditWordBtn;

    private ArrayList<Long> editWordColumnIdList;


    private Map<String, ColumnCmsEntry> unAssigedMap = null;

    public void set_columnList(ArrayList<ColumnCmsEntry> _columnList) {
        this._columnList = _columnList;
        sort(_columnList);
    }

    /**
     * 应急广播栏目
     */
    private ColumnCmsEntry yingjiColumn;

    //记录 推荐列表  Id
    private long homeNewsId = 0;

    public long getHomeNewsId() {
        return homeNewsId;
    }
    public void setHomeNewsId(long homeNewsId) {

        Log.e("setHomeNewsId: ",  "id: " + homeNewsId);
        this.homeNewsId = homeNewsId;
    }

    public ColumnCmsEntry getYingjiColumn() {
        if (yingjiColumn != null) {
            return findColumnByMachine(yingjiColumn.getMachine_code());
        }
        return yingjiColumn;
    }

    public void setYingjiColumn(ColumnCmsEntry yingjiColumn) {
        this.yingjiColumn = yingjiColumn;
    }

    public ColumnCmsEntry findColumnByMachine(String machine) {
        ColumnCmsEntry entry = null;
        if (unAssigedMap != null && !unAssigedMap.isEmpty()) {
            entry = unAssigedMap.get(machine);
        }
        return entry;
    }

    public void sort(List<ColumnCmsEntry> list) {
        int _pre_weight = -1;
        boolean _iSameWeight = true;
        if (list != null && !list.isEmpty()) {
//            for (ColumnCmsEntry obj : list) {
//                if (_pre_weight != -1) {
//                    if (_pre_weight != obj.getWeight()) {
//                        _iSameWeight = false;
//                        break;
//                    }
//                }
//                _pre_weight = obj.getWeight();
//            }
//            if (!_iSameWeight) {
            Collections.sort(list, new Comparator<ColumnCmsEntry>() {
                @Override
                public int compare(ColumnCmsEntry o1, ColumnCmsEntry o2) {
                    if (o1.getWeight() > o2.getWeight()) {
                        return -1;
                    } else if (o1.getWeight() < o2.getWeight()) {
                        return 1;
                    } else
                        return 0;
//                        return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
                }
            });
//                for (int i = 0; i < list.size(); i++) {
//                    ColumnCmsEntry pre = list.get(i);
//                    int k = i;
//                    while (k > 0) {
//                        ColumnCmsEntry  pp = list.get(k - 1);
//                        if (pp != null) {
//                            if (pre.getWeight() > pp.getWeight())
//                                list.add(k - 1, pre);
//                        }
//                        k--;
//                    }
//                }
//            }
        }
    }

    public boolean isEnbaleVoice() {
        return isEnbaleVoice;
    }

    public void setEnbaleVoice(boolean enbaleVoice) {
        isEnbaleVoice = enbaleVoice;
    }

    /**
     * 打开音量
     */
    public void openVolunes(Context context) {
        if (isEnbaleVoice) {
            VideoVoiceManager.getInstance(context).unmuteAudio();
            setEnbaleVoice(false);
        }
    }

    /**
     * 禁用音量
     */
    public void closedVolunes(Context context) {
        if (!isEnbaleVoice) {
            VideoVoiceManager.getInstance(context).muteAudio();
            setEnbaleVoice(true);
        }
    }

    public boolean isRershFlag() {
        return isRershFlag;
    }

    public void setRershFlag(boolean rershFlag) {
        isRershFlag = rershFlag;
    }

    public long getCmyId() {
        return cmyId;
    }

    public void setCmyId(long cmyId) {
        this.cmyId = cmyId;
    }

    public boolean isPraiseflag() {
        return isPraiseflag;
    }

    public void setPraiseflag(boolean praiseflag) {
        isPraiseflag = praiseflag;
    }

    /**
     * 根据栏目名称查找子栏目集合
     *
     * @param columnName
     * @return
     */
    public ArrayList<ColumnCmsEntry> findColumnListByName(String columnName) {
        ArrayList<ColumnCmsEntry> list = new ArrayList<>();
        if (columnName == null || TextUtils.isEmpty(columnName)) return list;
        String[] arr = columnName.split(",");
        if (arr == null || arr.length == 0) return list;
        if (_columnList != null && !_columnList.isEmpty()) {
            for (int i = 0; i < arr.length; i++) {
                for (ColumnCmsEntry item : _columnList) {
                    if (TextUtils.equals(item.getName(), arr[i])) {
                        if (TextUtils.equals("新闻", arr[i])) {
                            if (item.getDlist() != null && !item.getDlist().isEmpty()) {
                                for (ColumnCmsEntry child : item.getDlist()) {
                                    list.add(child);
                                }
                            }
                        } else
                            list.add(item);
                    }
                }
            }
        }
//        if (list != null && !list.isEmpty())
//            sort(list);
        return list;
    }

    /**
     * 根据栏目机器码查找子栏目集合
     *
     * @return
     */
//    public ArrayList<ColumnCmsEntry> findColumnListByCode(String codes) {
//        ArrayList<ColumnCmsEntry> list = new ArrayList<>();
//        if (codes == null || TextUtils.isEmpty(codes)) return list;
//        String[] arr = codes.split(",");
//        if (arr == null || arr.length == 0) return list;
//        if (_columnList != null && !_columnList.isEmpty()) {
//            for (int i = 0; i < arr.length; i++) {
//                for (ColumnCmsEntry item : _columnList) {
//                    if (TextUtils.equals(item.getMachine_code(), arr[i])) {
//                        if (TextUtils.equals("news", arr[i])) {
//                            if (item.getDlist() != null && !item.getDlist().isEmpty()) {
//                                for (ColumnCmsEntry child : item.getDlist()) {
//                                    list.add(child);
//                                }
//                            }
//                        } else
//                            list.add(item);
//                    }
//                }
//            }
//        }
//        if (!(list == null || list.isEmpty())) {
//            Collections.sort(list, new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
//                }
//            });
//            Collections.reverse(list);
//        }
//        return list;
//    }

    /**
     * 根据栏目机器码查找子栏目集合
     *
     * @return
     */
//    public List<ColumnCmsEntry> findColumnListByCodes(String codes) {
//        if (unAssigedMap == null) return null;
//        List<ColumnCmsEntry> list = null;
//        if (codes == null || TextUtils.isEmpty(codes)) return list;
//        String[] arr = codes.split(",");
//        if (arr == null || arr.length == 0) return list;
//        list = new ArrayList<>();
//        for (int i = 0; i < arr.length; i++) {
//            ColumnCmsEntry entry = unAssigedMap.get(arr[i]);
//            if (entry == null) continue;
//            if (TextUtils.equals(entry.getKey(), "home")) {
//                list.add(entry);
//                continue;
//            }
//            List<ColumnCmsEntry> data = entry.getDlist();
//            if (!(data == null || data.isEmpty())) {
//                for (ColumnCmsEntry op : data) {
//                    list.add(op);
//                }
//            }
//
//        }
//        if (!(list == null || list.isEmpty())) {
//            Collections.sort(list, new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
//                }
//            });
//            Collections.reverse(list);
//        }
//        return list;
//    }

    /**
     * 根据栏目机器码查找子栏目集合
     *
     * @return
     */
//    public List<ColumnCmsEntry> findColumnListByCodes(String codes) {
//        if (unAssigedMap == null) return null;
//        List<ColumnCmsEntry> list = null;
//        if (codes == null || TextUtils.isEmpty(codes)) return list;
//        String[] arr = codes.split(",");
//        if (arr == null || arr.length == 0) return list;
//        list = new ArrayList<>();
//        for (int i = 0; i < arr.length; i++) {
//            ColumnCmsEntry entry = unAssigedMap.get(arr[i]);
//            if (entry == null) continue;
//            if (TextUtils.equals(entry.getKey(), "home")
//                    || TextUtils.equals(entry.getMachine_code(),"home")
//                    || TextUtils.equals(entry.getName(),"推荐")) {
//                list.add(entry);
//                continue;
//            }
//            List<ColumnCmsEntry> data = entry.getDlist();
//            if (!(data == null || data.isEmpty())) {
//                for (ColumnCmsEntry op : data) {
//                    list.add(op);
//                }
//            }
//
//        }
//        sort(list);
////        if (!(list == null || list.isEmpty())) {
////            Collections.sort(list, new Comparator() {
////                @Override
////                public int compare(Object o1, Object o2) {
////                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
////                }
////            });
////            Collections.reverse(list);
////        }
//        return list;
//    }
    public List<ColumnCmsEntry> findColumnListByCodes(String codes) {
        if (unAssigedMap == null) {
            if (!(_columnList == null || _columnList.isEmpty())) {
                unAssigedMap = new HashMap<>();
                for (int i = 0; i < _columnList.size(); i++) {
                    ColumnCmsEntry entry = _columnList.get(i);
                    if (entry == null) continue;
                    unAssigedMap.put(entry.getMachine_code(), entry);
                }
            } else
                return null;
        }
        List<ColumnCmsEntry> list = null;
        if (codes == null || TextUtils.isEmpty(codes)) return list;
        String[] arr = codes.split(",");
        if (arr == null || arr.length == 0) return list;
        list = new ArrayList<>();
        ColumnCmsEntry recommond = null;
        for (int i = 0; i < arr.length; i++) {
            ColumnCmsEntry entry = unAssigedMap.get(arr[i]);
            if (entry == null) continue;
            if (TextUtils.equals(entry.getKey(), "home")
                    || TextUtils.equals(entry.getMachine_code(), "home")
                    || TextUtils.equals(entry.getName(), "推荐")) {
                recommond = entry;
                continue;
            }
            List<ColumnCmsEntry> data = entry.getDlist();
            if (!(data == null || data.isEmpty())) {
                for (ColumnCmsEntry op : data) {
                    list.add(op);
                }
                sort(list);
            }
        }
        if (recommond != null) {
            if (!list.isEmpty()) {
                list.add(0, recommond);
            } else
                list.add(recommond);
        }
//        sort(list);
//        if (!(list == null || list.isEmpty())) {
//            Collections.sort(list, new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
//                }
//            });
//            Collections.reverse(list);
//        }
        return list;
    }

    /**
     * 查找栏目  可用
     *
     * @param machineCode
     * @return
     */
    public ColumnCmsEntry findColumnByName(String machineCode) {
        ColumnCmsEntry entry = null;
        if (!(unAssigedMap == null || unAssigedMap.isEmpty())) {
            entry = unAssigedMap.get(machineCode);
        }
        return entry;
    }

    /**
     * 查找栏目Id
     *
     * @param key
     * @return
     */
    public List<ColumnCmsEntry> findDllListByKey(String key) {
        List<ColumnCmsEntry> dlist = null;
        if (!(unAssigedMap == null || unAssigedMap.isEmpty())) {
            dlist = unAssigedMap.get(key) == null ? null : unAssigedMap.get(key).getDlist();
        }
//        if (!(dlist == null || dlist.isEmpty())) {
//            Collections.sort(dlist, new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    return new Integer(((ColumnCmsEntry) o1).getWeight()).compareTo(new Integer(((ColumnCmsEntry) o2).getWeight()));
//                }
//            });
//            Collections.reverse(dlist);
//        }
        sort(dlist);
        return dlist;
    }

    /**
     * 查询栏目信息， 可能返回null
     *
     * @param machine_code
     * @param action
     */
    public void queryColumnEntry(final String machine_code, final Action1<ColumnCmsEntry> action) {
        if (unAssigedMap == null) {
            ColumnHelperManager columnHelper = new ColumnHelperManager(App.getInstance().getApplicationContext());
            columnHelper.getAllColumns(false);
            columnHelper.setCallback(new DataRequest.DataCallback<ArrayList<ColumnCmsEntry>>() {
                @Override
                public void onSuccess(boolean isAppend, ArrayList<ColumnCmsEntry> data) {
                    if (unAssigedMap != null) {
                        if (action != null) {
                            action.call(unAssigedMap.get(machine_code));
                        }
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    if (action != null) {
                        action.call(null);
                    }
                }
            });
        } else {
            if (action != null) {
                action.call(unAssigedMap.get(machine_code));
            }
        }
    }

    /**
     * 查找栏目
     *
     * @param id
     * @return
     */
    public ColumnCmsEntry findEntryById(long id) {
        ColumnCmsEntry item = null;
        if (!(unAssigedMap == null || unAssigedMap.isEmpty())) {
            Set<Map.Entry<String, ColumnCmsEntry>> entrySet = unAssigedMap.entrySet();
            for (Map.Entry<String, ColumnCmsEntry> entry : entrySet) {
                if (entry.getValue().getId() == id) {
                    item = entry.getValue();
                    break;
                }
//                System.out.println("取得键：" + entry.getKey());
//                System.out.println("对应的值为：" + entry.getValue().name);
            }
        }
        return item;
    }

    /**
     * 根据id获取child
     * @param id
     * @return
     */
    public List<ColumnCmsEntry> getDListById(long id){
        List<ColumnCmsEntry> dlist = null;
        ColumnCmsEntry item = findEntryById(id);
        if (item != null){
            dlist = item.getDlist();
            sort(dlist);
            return dlist;
        }
        return dlist;
    }

    /**
     * 查找栏目Id
     *
     * @param columnName
     * @return
     */
    public long findIdByName(String columnName) {
        return findColumnIdByName(columnName);
    }

    /**
     * 查找栏目Id
     *
     * @param machineCode
     * @return
     */
    public long findColumnIdByName(String machineCode) {
        long columnId = -1;
        if (unAssigedMap != null && !unAssigedMap.isEmpty()) {
            columnId = unAssigedMap.get(machineCode) == null ? -1 : unAssigedMap.get(machineCode).getId();
        }
        return columnId;
    }


    /**
     * 获取去掉置顶的栏目
     *
     * @param keys
     * @return
     */
    public ArrayList<ColumnCmsEntry> getListRemoveKey(String keys) {
        ArrayList<ColumnCmsEntry> list = null;
        if (_columnList == null || _columnList.isEmpty()) return list;
        ArrayList<ColumnCmsEntry> templist = _columnList;
        if (keys != null && !TextUtils.isEmpty(keys)) {
            String[] arr = keys.split(",");
            if (arr != null && arr.length > 0) {
                for (int i = 0; i < arr.length; i++) {
                    String key = arr[i];
                    for (ColumnCmsEntry tag : templist) {
                        if (TextUtils.equals(key, tag.getName())) {
                            templist.remove(tag);
                            break;
                        }
                    }
                }
                if (templist != null && !templist.isEmpty()) {
                    sort(templist);
                }
            }
        }
        return templist;
    }

    /**
     * 根据名称判断栏目是否存在
     *
     * @param key
     * @return
     */
    public boolean isExist(String key) {
        boolean flag = false;
        for (int i = 0; i < _recommedKsys.length; i++) {
            if (TextUtils.equals(key, _recommedKsys[i])) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void setUnAssigedMap(Map<String, ColumnCmsEntry> unAssigedMap) {
        this.unAssigedMap = unAssigedMap;
    }

    public boolean isShowEditWordBtn() {
        return isShowEditWordBtn;
    }

    public void setShowEditWordBtn(boolean showEditWordBtn) {
        isShowEditWordBtn = showEditWordBtn;
    }

    public ArrayList<Long> getEditWordColumnIdList() {
        return editWordColumnIdList;
    }

    public void setEditWordColumnIdList(ArrayList<Long> editWordColumnIdList) {
        this.editWordColumnIdList = editWordColumnIdList;
    }

    public ArrayList<LiveEntity.LiveChannel> getLiveTvChannelMap() {
        return liveTvChannelMap;
    }

    public void setLiveTvChannelMap(ArrayList<LiveEntity.LiveChannel> liveTvChannelMap) {
        this.liveTvChannelMap = liveTvChannelMap;
    }

    public LiveEntity.LiveChannel getSelectliveTvChanne() {
        return selectliveTvChanne;
    }

    public void setSelectliveTvChanne(LiveEntity.LiveChannel selectliveTvChanne) {
        this.selectliveTvChanne = selectliveTvChanne;
    }

}
