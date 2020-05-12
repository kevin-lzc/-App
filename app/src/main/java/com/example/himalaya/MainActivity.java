package com.example.himalaya;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.Adapter.MainContentAdapter;
import com.example.himalaya.Data.XimalayaDBHelper;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenter.PlayerPresenter;
import com.example.himalaya.presenter.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.sunofbeaches.himalaya.views.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;


public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private com.sunofbeaches.himalaya.views.RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private TextView mSubTitle;
    private View mSearchBtn;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   initView();
   initEvent();
   initPresenter();

    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                if (mContentPager!=null) {
                    mContentPager.setCurrentItem(index,false);
                }

            }
        });
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                   //播放第一张专辑
                        playListRecommend();
                    }else{
                        if (mPlayerPresenter.isPlaying() ) {
                            mPlayerPresenter.pause();
                        }else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //播放第一个推荐内容
    private void playListRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null&&currentRecommend.size()>0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        //创建Indicator适配器
        mMagicIndicator = this.findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator=new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);//自我调节Indicator的位置，平分
        commonNavigator.setAdapter(mIndicatorAdapter);
        //将ViewPager导入
         mContentPager = this.findViewById(R.id.content_pager);
         //创建ViewPager的监听调用addOnPageChangeListener()方法
        mContentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //创建ViewPager碎片的适配器
        FragmentManager supportFragmentManager=getSupportFragmentManager();
        MainContentAdapter maincontentAdapter=new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(maincontentAdapter);
        //把mMagicIndicator和 mContentPager绑定起来让他们一起滑动
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);


        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHeaderTitle = this.findViewById(R.id.main_header_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        mSearchBtn = this.findViewById(R.id.search_Btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        UpdatePlayControl(true);
    }

    public void UpdatePlayControl(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying?R.drawable.selector_palyer_pause:R.drawable.selector_palyer_play);
        }
    }
    @Override
    public void onPlayPause() {
        UpdatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        UpdatePlayControl(false);
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
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            Log.d(TAG,"1"+trackTitle);
            Log.d(TAG,"1"+nickname);
            Log.d(TAG,"1"+coverUrlMiddle);

            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}