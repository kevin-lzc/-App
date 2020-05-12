package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {
    //搜索结果获取专辑列表的回调
    void onSearchResultLoaded(List<Album> result);
    //获取推荐热词的回调
    void onHotWordLoaded(List<HotWord> hotWordList);
    //加载更多结果返回
    void onLoadedMoreResult(List<Album> result,boolean isOkay);
    //联想关键字的结果
    void onRecommendWordLoaded(List<QueryResult> keyWordList);
    //错误通知回调
    void onError(int errorCode,String errorMsg);
}
