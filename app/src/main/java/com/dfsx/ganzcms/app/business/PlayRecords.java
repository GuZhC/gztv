package com.dfsx.ganzcms.app.business;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import com.dfsx.ganzcms.app.model.SingleRecord;
import com.dfsx.ganzcms.app.util.MyJsonHelper;
import com.dfsx.ganzcms.app.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by halim on 2015/8/5.
 */
public class PlayRecords {

    static final String  PLAY_RECORDS_TAG = "PLAY_HISTORY";

    public PlayRecords()
    {
        mMap = new HashMap<String, List<SingleRecord>>();
    }


    private Map<String ,List<SingleRecord>> mMap;


    public  void ReadFromReference(String uid)
    {
        if(uid == null)
            return;
        List<SingleRecord> list = null;
        SharedPreferences mySharedPreferences= App.getInstance().getSharedPreferences(uid + PLAY_RECORDS_TAG, Activity.MODE_PRIVATE);
        if(mySharedPreferences == null )
            return;
        String listStr;
        listStr = mySharedPreferences.getString(PLAY_RECORDS_TAG,"");
        try {
            list = (List) MyJsonHelper.parseCollection(listStr, List.class, SingleRecord.class);
        }
        catch (Exception ex)
        {
            Log.d(PLAY_RECORDS_TAG,ex.getMessage());
        }
        if(list != null)
            mMap.put(uid,list);


    }

    public List<SingleRecord> getListByUserId(String uid)
    {
        if(!mMap.containsKey(uid))
           return null;
        else
            return  mMap.get(uid);
    }

    public void  SaveToReference(String uid)
    {
        List<SingleRecord> list;
        SingleRecord sr;
        if(mMap.containsKey(uid))
        {
            SharedPreferences mySharedPreferences= App.getInstance().getSharedPreferences(uid + PLAY_RECORDS_TAG, Activity.MODE_PRIVATE);
            if(mySharedPreferences == null )
                return;
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            list = mMap.get(uid);
            String jsonStrList = MyJsonHelper.toJSON(list);
            editor.putString(PLAY_RECORDS_TAG, jsonStrList);
            editor.commit();
            return;
        }

    }

    public void AppendRecord(String uid , SingleRecord record)
    {
        String key;
        List<SingleRecord> entry;
        SingleRecord sr;
        if(!mMap.containsKey(uid))
        {
            entry = new ArrayList<SingleRecord>();
            mMap.put( uid , entry );
            entry.add(record);
            return;
        }

        entry = mMap.get(uid);
        for(int i = 0; i < entry.size(); i++)
        {
            sr = entry.get(i);
            if(sr.id == record.id)
            {
                return;
            }
        }
        entry.add(record);

    }


}
