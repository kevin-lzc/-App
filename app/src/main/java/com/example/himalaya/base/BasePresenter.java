package com.example.himalaya.base;

public interface BasePresenter<T> {

    /*
   这个方法用于注册UI的回调
    */
    void registerViewCallback(T t);
    /*
  这个方法用于取消注册UI的回调
   */
    void unRegisterViewCallback(T t);
}
