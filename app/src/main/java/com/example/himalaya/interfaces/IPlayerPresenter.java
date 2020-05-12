package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends BasePresenter<IPlayerCallback> {

    void play();//播放
    void pause();//暂停
    void stop();//停止
    void playPre();//播放上一首
    void playNext();//播放下一首
    void switchPlayMode(XmPlayListControl.PlayMode mode);//切换播放模式
    void getPlayList();//获取播放列表
    void playByIndex(int index);//根据节目的位置进行播放
    void seekTo(int progress);//切换播放进度条
    boolean isPlaying();//判断播放器是否在播放
    void reversePlayList();//播放顺序反转
    void playByAlbumId(long id);//播放专辑的第一首歌

}
