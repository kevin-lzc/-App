package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptionPresenter extends BasePresenter<ISubscriptionCallback> {
    /*
    添加订阅
     */
    void addSubscription(Album album);
    /*
    删除订阅
     */
    void deleteSubscription(Album album);
    /*
    获取订阅列表
     */
    void getSubscriptionList();
    /*
    判断当前的专辑是否已经收藏
     */
   boolean isSub(Album album);
}
