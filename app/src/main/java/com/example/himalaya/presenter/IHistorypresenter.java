package com.example.himalaya.presenter;

import com.example.himalaya.base.BasePresenter;
import com.example.himalaya.interfaces.IHisotryCallback;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistorypresenter extends BasePresenter<IHisotryCallback> {
/*
获取历史 内容
 */
void listHistories();
/*
添加历史内容
 */
void addHistory(Track track);
/*
删除历史内容
 */
void delHistory(Track track);
/*
清楚历史内容
 */
void cleanHistory();

}
