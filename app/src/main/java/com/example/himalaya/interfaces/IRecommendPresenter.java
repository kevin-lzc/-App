package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

/*
用于代码重构，将推荐页面的逻辑和UI分开
 */
public interface IRecommendPresenter extends BasePresenter<IRecommendViewCallback> {
    /*
    获取推荐内容
     */
    void getReCommendList();
    /*
    下拉刷新更多内容
     */
    void pull2RefreshMore();
    /*
    上接加载更多
     */
    void loadMore();


    /*
    取消UI的回调注册
     */

}
