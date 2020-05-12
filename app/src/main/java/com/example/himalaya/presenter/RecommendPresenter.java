package com.example.himalaya.presenter;

import com.example.himalaya.Data.XimalayaApi;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {
    private List<IRecommendViewCallback> mCallbacks=new ArrayList<>();
    private List<Album> mCurrentRecommend= null;
    private List<Album> mRecommendList;
    private RecommendPresenter(){};
    private static RecommendPresenter sInstance=null;

    /*
    获取单例对象，使用单例模式中的懒汉式
     */

    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }
    /*
    获取当前的专辑列表
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }
    @Override
    public void getReCommendList() {
        if (mRecommendList != null&&mRecommendList.size()>0) {
            handlerRecommendResult(mRecommendList);
            return;
        }
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();

        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {



            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList!=null) {//判断如果说数据不等于空
                    mRecommendList = gussLikeAlbumList.getAlbumList();
//数据传回来之后更新UI
                    //upRecommendUI(albumsList);
                    handlerRecommendResult(mRecommendList);
                }
            }
            @Override
            public void onError(int i, String s) {
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks!=null) {
            for(IRecommendViewCallback callback:mCallbacks){
                callback.onNetworkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumsList) {
//通知UI更新
        //这种情况是返回数据为空
        if (albumsList != null) {
            if (albumsList.size() == 0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }

            } else {//这种是返回数据成功
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumsList);
                }
                this.mCurrentRecommend=albumsList;
            }
        }
    }
    private void updateLoading(){
        for (IRecommendViewCallback callback:mCallbacks) {
            callback.onLoading();
            
        }
    }
    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks!=null) {
            mCallbacks.remove(callback);
        }
    }
}
