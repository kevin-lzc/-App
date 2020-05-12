package com.example.himalaya.presenter;

import com.example.himalaya.Data.SubscriptionDao;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.base.ISubDaoCallback;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.interfaces.ISubscriptionPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private static final String TAG = "SubscriptionPresenter";
    private final SubscriptionDao mSubscriptionDao;
private Map<Long,Album> mData=new HashMap<>();
private List<ISubscriptionCallback> mCallbacks=new ArrayList<>();
    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
}

private void listSubscriptions(){
    Observable.create(new ObservableOnSubscribe<Object>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
//只调用，不处理结果
            if (mSubscriptionDao != null) {
                mSubscriptionDao.ListAlbums();

            }
        }
    }).subscribeOn(Schedulers.io()).subscribe();
}
    private static SubscriptionPresenter sInstance = null;

    public static SubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                    sInstance = new SubscriptionPresenter();

            }
        }
        return sInstance;
    }
    @Override
    public void addSubscription(final Album album) {
//判断当前的订阅数量
        if (mData.size()>= Constants.MAX_SUB_COUNT) {
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubFull();
            }
        }
      Observable.create(new ObservableOnSubscribe<Object>() {
    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
        if (mSubscriptionDao != null) {
            mSubscriptionDao.addAlbum(album);
        }
         }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.deAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        return result!=null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        /*
       添加结果的回调
         */

        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "update ui for add result.");
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDeleteResult(final boolean isSuccess) {
        listSubscriptions();
/*
删除订阅的回调
 */
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
/*
加载的回调
 */
mData.clear();
        for (Album album : result) {
            mData.put(album.getId(),album);
        }
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscription(result);
                }
            }
        });
    }
}
