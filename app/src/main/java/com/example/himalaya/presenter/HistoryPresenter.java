package com.example.himalaya.presenter;

import com.example.himalaya.Data.HistoryDao;
import com.example.himalaya.Data.IHistoryDao;
import com.example.himalaya.Data.IHistoryDaoCallback;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IHisotryCallback;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryPresenter implements IHistorypresenter, IHistoryDaoCallback {
    //定义集合保存UI注册回来的结果
private List<IHisotryCallback> mCallbacks=new ArrayList<>();
    private final IHistoryDao mHistoryDao;
    private List<Track> mCurrentHistories=null;
    private Track mCurrentAddTrack=null;

    //设计一个单例
    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }
    private static HistoryPresenter sHistoryPresenter=null;
    public static HistoryPresenter getHistoryPresenter(){
        if (sHistoryPresenter==null) {
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter==null) {
                    sHistoryPresenter=new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }
    @Override
    public void listHistories() {
        /*
        1.Rxjava被观察者模式
        创建被观察者
         */
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

private boolean isDoDelAsOutOfSize =false;
    @Override
    public void addHistory(Track track) {
        //判断历史记录是否超过100条
        if (mCurrentHistories != null&&mCurrentHistories.size()>= Constants.MAX_HISTORY_COUNT) {
//先不能添加历史记录，先删除最前的一条历史记录。在添加
           isDoDelAsOutOfSize =true;
            this.mCurrentAddTrack=track;
            delHistory(mCurrentHistories.get(mCurrentHistories.size()-1));
        }else {
            doAddHistory(track);

        }
    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
            }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);

            }}
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
            }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHisotryCallback iHisotryCallback) {
       if(!mCallbacks.contains(iHisotryCallback)) {
           mCallbacks.add(iHisotryCallback);
       }
    }

    @Override
    public void unRegisterViewCallback(IHisotryCallback iHisotryCallback) {
mCallbacks.remove(iHisotryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
     //添加当前的新添加的历史数据到数据库
        if (isDoDelAsOutOfSize &&mCurrentAddTrack!=null) {
            isDoDelAsOutOfSize =false;
            addHistory(mCurrentAddTrack);
        }else {
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
//通知UI更新数据
        this.mCurrentHistories=tracks;
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHisotryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        listHistories();
    }
}
