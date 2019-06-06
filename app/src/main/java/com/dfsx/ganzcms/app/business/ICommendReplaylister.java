package com.dfsx.ganzcms.app.business;

import android.view.View;


//  create  by  henayng  2017-9-27
//  回复评论点击  回调


public interface ICommendReplaylister<T> {
    public void OnItemClick(View v, T object);
}