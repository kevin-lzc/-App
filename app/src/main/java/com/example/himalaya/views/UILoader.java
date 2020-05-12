package com.example.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyErrorView;
    private OnRetryClickListener mOnRetryClickListener=null;


    /*
            定义枚举类，放入数据返回的几种状态
             */
    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStatus =UIStatus.NONE;
    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
    public void updateStatus(UIStatus status){
        mCurrentStatus=status;
        //更新UI在主线程上
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }
/*
初始化UI
 */
    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView==null) {
            mLoadingView = (View) getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus==UIStatus.LOADING?VISIBLE:GONE);
        //成功
        if (mSuccessView==null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        //网络错误界面
        if (mNetworkErrorView==null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        mNetworkErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);
        //数据为空的界面
        if (mEmptyErrorView==null) {
            mEmptyErrorView = getEmpty();
            addView(mEmptyErrorView);
        }
        //根据状态设置是否可见
        mEmptyErrorView.setVisibility(mCurrentStatus==UIStatus.EMPTY?VISIBLE:GONE);

    }

    protected View getEmpty() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragement_empty_view,this,false);
    }

    private View getNetworkErrorView() {
        View networkErrorView=LayoutInflater.from(getContext()).inflate(R.layout.fragement_error_view,this,false);
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryClickListener!=null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return networkErrorView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private Object getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragement_loading_view,this,false);
    }
    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mOnRetryClickListener=listener;
    }
    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
