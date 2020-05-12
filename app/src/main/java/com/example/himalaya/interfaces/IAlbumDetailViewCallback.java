package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;
/*
加载专辑详情
 */
public interface IAlbumDetailViewCallback {
    void onDetailListLoaded(List<Track> tracks);
    void onNetworkError(int errorCode, String errorMsg);
    void onAlbumLoaded(Album album);
    /*
    加载更多的接口 size>0表示加载失败，<0表示加载失败
     */
    void onLoadMoreFinished(int size);
    /*
    下拉加载更多的结果 size>0表示加载失败，<0表示加载失败
     */
    void RefreshFinished(int size);
}
