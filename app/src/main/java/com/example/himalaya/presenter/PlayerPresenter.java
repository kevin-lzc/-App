package com.example.himalaya.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.himalaya.Data.XimalayaApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
private List<IPlayerCallback> mIPlayerCallbacks=new ArrayList<>();
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mPlayModeSp;
    private boolean misReverse =false;
    private XmPlayListControl.PlayMode mCurrentPlayMode= PLAY_MODEL_LIST;
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    public static final String PLAY_MODE_SP_NAME="PlayMode";
    public static final String PLAY_MODE_SP_KEY="currentPlayMode";
    private int mCurrentProgressPosition=0;
    private int mProgressDuration=0;


    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
      mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);

        //mPlayModSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);*/
    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getPlayerPresenter() {
        if(sPlayerPresenter == null) {
            synchronized(PlayerPresenter.class) {
                if(sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }
    private boolean isPlayListSet=false;
public void setPlayList(List<Track> list, int playIndex){
    if (mPlayerManager != null) {
        mPlayerManager.setPlayList(list,playIndex);
        isPlayListSet=true;
        mCurrentTrack=list.get(playIndex);
        mCurrentIndex=playIndex;
    }else {
        LogUtil.d(TAG,"555");
    }

}
    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager!=null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }
public boolean hasPlayList(){

    return isPlayListSet;
}

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode=mode;
            mPlayerManager.setPlayMode(mode);
            //通知UI更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到SP里面
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMOde(mode));
            edit.commit();
        }
    }
    private int getIntByPlayMOde(XmPlayListControl.PlayMode mode){
    switch (mode){
        case PLAY_MODEL_SINGLE_LOOP :
            return PLAY_MODEL_SINGLE_LOOP_INT;
        case  PLAY_MODEL_LIST_LOOP:
            return PLAY_MODEL_LIST_LOOP_INT;
        case  PLAY_MODEL_RANDOM:
            return PLAY_MODEL_RANDOM_INT;
        case  PLAY_MODEL_LIST:
            return  PLAY_MODEL_LIST_INT;
    }
    return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getMOdeByInt(int index){
        switch (index){
            case  PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case   PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case   PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case   PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST ;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }

    }

    @Override
    public void playByIndex(int index) {
//切换播放器到第Index的位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }

    }

    @Override
    public void seekTo(int progress) {
//更新播放器的进度
mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
    /*
    返回当时是否在播放
     */
    return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
    //将播放顺序反转
        List<Track> playList = mPlayerManager.getPlayList();//获取播放列表
        Collections.reverse(playList);//反转播放列表
        misReverse =!misReverse;
        mCurrentIndex=playList.size()-1-mCurrentIndex;//求出当前下标反转之后的下标
        mPlayerManager.setPlayList(playList,mCurrentIndex);//设置
        //更新UI
        mCurrentTrack= (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(misReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
//1.获取专辑的列表内容
        XimalayaApi ximalayaApi=XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
//2.把专辑的内容设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (trackList != null&&tracks.size()>0) {
mPlayerManager.setPlayList(tracks,DEFAULT_PLAY_INDEX);
                    isPlayListSet=true;
                    mCurrentTrack=tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex=DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int errorCode,String errorMsg) {
                LogUtil.d(TAG,"errorCode -- > " + errorCode);
                LogUtil.d(TAG,"errorMsg -- > " + errorMsg);
                Toast.makeText(BaseApplication.getAppContext(),"请求数据错误...",Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);

//3.开始播放
//
    }


    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    //更新UI之前获取数据
        getPlayList();
    //通知当前节目
    iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
    iPlayerCallback.onProgressChange(mCurrentProgressPosition,mProgressDuration);
    //更新状态
        handlePlayState(iPlayerCallback);
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
      mCurrentPlayMode = getMOdeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        if (PlayerConstants.STATE_STARTED==playerStatus) {
            iPlayerCallback.onPlayStart();
        }else {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
mIPlayerCallbacks.remove(iPlayerCallback);
    }





    //广告回调
    @Override
    public void onStartGetAdsInfo() {
LogUtil.d(TAG,"777777");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"564646");
    }

    @Override
    public void onAdsStartBuffering() {

    }

    @Override
    public void onAdsStopBuffering() {

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {

    }

    @Override
    public void onCompletePlayAds() {

    }

    @Override
    public void onError(int i, int i1) {

    }




    //播放器相关回调
    @Override
    public void onPlayStart() {
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }
    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {
    mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备完成
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        if (lastModel != null) {

        }
mCurrentIndex=mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack=(Track) curModel;
            mCurrentTrack=currentTrack;
//保存播放记录
            HistoryPresenter historyPresenter = HistoryPresenter.getHistoryPresenter();
            historyPresenter.addHistory(currentTrack);
            //更新UI
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            }
        }
//当当前的节目改变以后，修改当前的图片

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }

    @Override
    public void onPlayProgress(int currPos, int duration){
    this.mCurrentProgressPosition=currPos;
    this.mProgressDuration=duration;
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }
}
