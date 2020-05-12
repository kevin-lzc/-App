package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback  {
    void onPlayStart();
    void onPlayPause();
    void onPlayStop();
    void onPlayError();
    void nextPlay(Track track);
    void onPrePlay(Track track);
    void onListLoaded(List<Track> list);
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);
    void onProgressChange(int currentProgress,int  total);
    void onAdLoading();
    void onAdFinished();
    void onTrackUpdate(Track track,int playIndex);
    //更新顺序播放的文字和图标
    void updateListOrder(boolean isReverse);
}
