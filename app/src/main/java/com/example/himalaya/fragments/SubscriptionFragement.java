package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.AlbumListAdapter;
import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.base.Basefragment;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.presenter.AlbumDetailPresenter;
import com.example.himalaya.presenter.SubscriptionPresenter;
import com.example.himalaya.views.ConfirmDialog;
import com.example.himalaya.views.DetailActivity;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragement extends Basefragment implements ISubscriptionCallback, AlbumListAdapter.onAlbumItemClickListener, AlbumListAdapter.onAlbumItemLongClickListener, ConfirmDialog.onDialogActionClickListener {
    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;
    private Album mCurrentClickAlbum=null;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragementsubscription,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmpty() {
                    //创建一个新的
                    View emptyView= LayoutInflater.from(getContext()).inflate(R.layout.fragement_empty_view,this,false);
                    TextView tipsView=emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent()instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }

        return rootView;

    }

    private View createSuccessView() {
        View itemView=LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription,null);
        TwinklingRefreshLayout refreshLayout=itemView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        mSubListView = itemView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setAlbumItemClickListener(this);
        mAlbumListAdapter.setonAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mAlbumListAdapter);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
     Toast.makeText(BaseApplication.getAppContext(),isSuccess?R.string.cancel_sub_success:R.string.cancel_sub_failed,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscription(List<Album> albums) {
        if (albums.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else{
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
        if (mAlbumListAdapter != null) {
            //使添加订阅的专辑处于订阅栏的第一个位置
            mAlbumListAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getActivity(),"订阅数量不得超过100",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        mAlbumListAdapter.setAlbumItemClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentClickAlbum=album;
  //Toast.makeText(BaseApplication.getAppContext(),"订阅被长按",Toast.LENGTH_SHORT).show();
        ConfirmDialog confirmDialog=new ConfirmDialog(getActivity());
        confirmDialog.setonDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //取消订阅
        if (mCurrentClickAlbum != null && mSubscriptionPresenter!=null) {
          mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
        }
    }

    @Override
    public void onGiveUpClick() {
        //放弃取消订阅
    }
}
