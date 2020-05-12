package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.AlbumListAdapter;
import com.example.himalaya.R;
import com.example.himalaya.base.Basefragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenter.AlbumDetailPresenter;
import com.example.himalaya.presenter.RecommendPresenter;
import com.example.himalaya.views.DetailActivity;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragement extends Basefragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.onAlbumItemClickListener {


    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiloader;
    private View mRootView;


    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        mUiloader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return CreateSuccessView(layoutInflater,container);
            }
        };
        //view加载完成


        //获取到逻辑层的对象
        mRecommendPresenter=RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getReCommendList();
        //返回View给界面显示
        if (mUiloader.getParent()instanceof ViewGroup) {
         ((ViewGroup) mUiloader.getParent()).removeView(mUiloader);
        }
        mUiloader.setOnRetryClickListener(this);
        return mUiloader;

    }

    private View CreateSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        mRootView = layoutInflater.inflate(R.layout.fragementrecommend,container,false);
        //recyclerview使用步骤
        //第一步：找到控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout=mRootView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        //第二部：设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //第三步设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
mRecommendListAdapter.setAlbumItemClickListener(this);
        return mRootView;
    }
    /*获取推荐内容，接入SDK中的接口----3.10.6 获取猜你喜欢专辑
* */


    @Override
    public void onRecommendListLoaded(List<Album> result) {
//当我们获取到推荐内容的时候，这个方法就会被调用
        mRecommendListAdapter.setData(result);
        mUiloader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUiloader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }



    @Override
    public void onEmpty() {
        mUiloader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiloader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.getReCommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        /*
        Item条目被点击，然后进入详情界面
         */
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
Intent intent=new Intent(getContext(), DetailActivity.class);
startActivity(intent);
    }
}
