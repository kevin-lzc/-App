package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

public interface IAlbumDetailPresenter extends BasePresenter<IAlbumDetailViewCallback> {
    /*
  下拉刷新更多内容
   */
    void pull2RefreshMore();

    /*
    上接加载更多
     */
    void loadMore();

    void getAlbumDetail(int albumId, int page);
}
    /*
     */