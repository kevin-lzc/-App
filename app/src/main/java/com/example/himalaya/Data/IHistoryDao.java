package com.example.himalaya.Data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {
    /*
   设置回调接口
    */
    void setCallback(IHistoryDaoCallback callback);
    /*
    添加听过的歌曲
     */
    void addHistory(Track track);
    /*
    删除历史
     */
    void delHistory(Track track);
    /*
    清除历史
     */
    void clearHistory();
    /*
    获取历史内容
     */
    void listHistories();
}
