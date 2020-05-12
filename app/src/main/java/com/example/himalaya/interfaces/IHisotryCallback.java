package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHisotryCallback {
    /*
    历史内容的加载结果
     */
void onHistoriesLoaded(List<Track> tracks);
}
