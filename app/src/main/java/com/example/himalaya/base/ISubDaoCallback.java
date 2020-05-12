package com.example.himalaya.base;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {
    /*
    添加的结果回调
     */
    void onAddResult(boolean isSuccess);
    /*
    删除结果的回调方法
     */
    void onDeleteResult(boolean isSuccess);
    /*
    查询结果回调方法
     */
    void onSubListLoaded(List<Album> result);
}
