package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {
/*
调用添加的时候，去通知UI结果
 */
    void onAddResult(boolean isSuccess);
    /*
    删除订阅的回调方法
     */
void onDeleteResult(boolean isSuccess);
/*
订阅专辑加载的结果回调方法
 */
void onSubscription(List<Album> albums);
/*
订阅数量满了
 */
void onSubFull();
}
