package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.TrackListAdapter;
import com.example.himalaya.PlayerActivity;
import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.base.Basefragment;
import com.example.himalaya.interfaces.IHisotryCallback;
import com.example.himalaya.presenter.HistoryPresenter;
import com.example.himalaya.presenter.PlayerPresenter;
import com.example.himalaya.views.ConfirmCheckBoxDialog;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragement extends Basefragment implements IHisotryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.onDialogActionClickListener {
    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem=null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragementhistory, container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmpty() {

                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragement_empty_view, this, false);
                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("没有历史记录");
                    return  emptyView;
                }
            };

        }else {
            if (mUiLoader.getParent()instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistories();
        rootView.addView(mUiLoader);
        return rootView;

    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(getContext()).inflate(R.layout.item_history, container, false);
        TwinklingRefreshLayout refreshLayout=successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableOverScroll(true);
        //recyclerView
        RecyclerView historylist=successView.findViewById(R.id.history_list);
        historylist.setLayoutManager(new LinearLayoutManager(container.getContext()));
        historylist.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //设置适配器
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        historylist.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks==null||tracks.size()==0) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {

            //更新数据
            mTrackListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        PlayerPresenter playerPresenter=PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData,position);
        /*
        跳转到播放器界面
         */
        Intent intent=new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem=track;
        //删除历史
        // Toast.makeText(getActivity(),"455",Toast.LENGTH_SHORT).show();
        ConfirmCheckBoxDialog dialog=new ConfirmCheckBoxDialog(getActivity());
        dialog.setonDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {

    }

    @Override
    public void onConfirmClick(boolean isCheck) {
        if (mHistoryPresenter != null&&mCurrentClickHistoryItem!=null) {
            if (!isCheck) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            }else {
                mHistoryPresenter.cleanHistory();
            }
        }
    }
}
