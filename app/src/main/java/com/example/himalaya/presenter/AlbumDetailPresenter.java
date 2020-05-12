package com.example.himalaya.presenter;

import com.example.himalaya.Data.XimalayaApi;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private List<IAlbumDetailViewCallback> mCallbacks=new ArrayList<>();
    private List<Track> mTracks=new ArrayList<>();
    private Album mTargetAlbum=null;
    //当前的专辑Id
     private int mCurrentAlbumId=-1;
     //当前页
     private int mCurrentPageIndex=0;

     private AlbumDetailPresenter() {

    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
//加载更多内容
mCurrentPageIndex++;
doLoad(true);
    }
    private void doLoad( final boolean isLoadMore){
        XimalayaApi ximalayaApi=XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList!=null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoadMore) {
//上拉加载数据放到后面去
                        mTracks.addAll(tracks);
                        int size = tracks.size();
handlerLoaderMoreResult(size);
                    }else {
                        //这个是下拉刷新，数据放到前面去
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode,String errorMsg ) {
                if (isLoadMore) {
                    mCurrentPageIndex--;
                }
                handlerError(errorCode,errorMsg);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

     private void handlerLoaderMoreResult(int size) {
         for (IAlbumDetailViewCallback callback : mCallbacks) {
             callback.onLoadMoreFinished(size);
         }
     }

     @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId=albumId;
        this.mCurrentPageIndex=page;
       doLoad(false);
    }

    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode,errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum!=null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }

        }
    }

     @Override
     public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
         mCallbacks.remove(detailViewCallback);

     }


//     @Override
//    public void UnregisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
//        mCallbacks.remove(detailViewCallback);
//
//    }


    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum=targetAlbum;
    }
}


