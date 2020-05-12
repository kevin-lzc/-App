package com.example.himalaya.interfaces;

import com.example.himalaya.base.ISubDaoCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDao {
    void setCallback(ISubDaoCallback callback);
    /*
    添加订阅
     */
    void addAlbum(Album album);
    /*
    删除订阅内容
     */
    void deAlbum(Album album);
    /*
    获取订阅内容
     */
    void ListAlbums();
}
