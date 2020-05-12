package com.example.himalaya.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.TrackListAdapter;
import com.example.himalaya.PlayerActivity;
import com.example.himalaya.R;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.presenter.AlbumDetailPresenter;
import com.example.himalaya.presenter.PlayerPresenter;
import com.example.himalaya.presenter.SubscriptionPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sunofbeaches.himalaya.views.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, TrackListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {
    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage=1;
    private RecyclerView mDetailList;
    private TrackListAdapter mDetailListAdapter;
    private UILoader mUiLoader;
    private FrameLayout mDetailListContainer;
    private  long  mCurrentId=-1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks=null;
private static final int DEFAULT_PLAY_INDEX=0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum=null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        initPresenter();
        //设置订阅按钮的状态
        updateSubState();
        updatePlayState(mPlayerPresenter.isPlaying());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(sub ?R.string.cancel_sub_tips_text:R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        handlePlayControl();
                    }else {
handleNoPlayList();
                    }

                }
                //控制播放器的状态

            }
        });
        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    }else{
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }
//当播放器里面没有内容，进行处理
    private void handleNoPlayList() {
mPlayerPresenter.setPlayList(mCurrentTracks,DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        }else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        if (mUiLoader==null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
        mSubBtn = this.findViewById(R.id.detail_sub_btn);
    }
private boolean mIsLoaderMore=false;
    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        mDetailListAdapter = new TrackListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);

        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        BezierLayout headView=new BezierLayout(this);
        mRefreshLayout.setHeaderView(headView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this,"开始刷新",Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore=true;
                }

//                BaseApplication.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(DetailActivity.this,"下载更多",Toast.LENGTH_SHORT).show();
//                        mRefreshLayout.finishLoadmore();
//                    }
//                },2000);
            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore&&mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore=false;
        }
        this.mCurrentTracks=tracks;
        if (tracks==null||tracks.size()==0) {
            if (mUiLoader!=null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);

        }

/*
更新UI
 */
mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum=album;
        long id=album.getId();
        mCurrentId=id;
        //获取详情列表
        if (mAlbumDetailPresenter!=null) {
            mAlbumDetailPresenter.getAlbumDetail((int) id,mCurrentPage);
        }

        if (mUiLoader!=null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitle!=null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor!=null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mLargeCover!=null&&null != mLargeCover) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
        if (mSmallCover!=null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onLoadMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this,"成功加载",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"没有更多节目",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void RefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        if (mAlbumDetailPresenter!=null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId,mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        PlayerPresenter playerPresenter=PlayerPresenter.getPlayerPresenter();
       playerPresenter.setPlayList(detailData,position);
        /*
        跳转到播放器界面
         */
        Intent intent=new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
    private void updatePlayState(boolean playing) {
        if (mPlayControlBtn != null&&mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing?R.drawable.selector_play_control_pause:R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTips.setText( R.string.click_play_tips_text);
            }else {
                if(!TextUtils.isEmpty(mCurrentTrackTitle)){
                    mPlayControlTips.setText(mCurrentTrackTitle);
                }
            }
        }
    }
    @Override
    public void onPlayStart() {
        //修改图标成为暂停，文字修改为正在播放
        updatePlayState(true);


    }

    @Override
    public void onPlayPause() {
        //修改图标成为播放，文字修改为正在暂停
        updatePlayState(false);

    }


    @Override
    public void onPlayStop() {
        updatePlayState(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (mPlayControlTips != null&&!TextUtils.isEmpty(mCurrentTrackTitle)) {

                mPlayControlTips.setText(mCurrentTrackTitle);
            }

        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            /*
如果成功就修改UI成取消订阅状态
 */
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        String tipsText=isSuccess? "订阅成功":"订阅失败";
Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            /*
如果成功就修改UI成取消订阅状态
 */
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String tipsText=isSuccess? "删除成功":"删除失败";
        Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSubscription(List<Album> albums) {

    }

    @Override
    public void onSubFull() {
Toast.makeText(this,"订阅数量不得超过100",Toast.LENGTH_SHORT).show();
    }
}
