package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;

public interface ISearchPresenter extends BasePresenter<ISearchCallback> {
    //进行搜索
    void doSearch(String keyWord);
    //重新搜索
    void reSearch();
    //加载更多
    void LoadMore();
    //得到热词
    void getHotWord();
    //得到推荐列表
void getRecommendWord(String KeyWord);
}
