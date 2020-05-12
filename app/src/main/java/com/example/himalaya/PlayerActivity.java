package com.example.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.himalaya.Adapter.PlayerTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenter.PlayerPresenter;
import com.example.himalaya.views.SobPopWindows;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {
    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat minFormat=new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat=new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress=0;
    private boolean mIsUserTouchProgressBar=false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager=false;
    private ImageView mPlatModeSwitchBtn;
    //默认的当前MOde
    private XmPlayListControl.PlayMode mCurrentMode=XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
private  static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRule=new HashMap<>();
static{
    sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
    sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
    sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
    sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
}

    private View mPlayListenBtn;
    private SobPopWindows mSobPopWindows;
    private ValueAnimator mEnterBgAnmator;
    private ValueAnimator mOutBgAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
       initEvent();
      initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnmator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnmator.setDuration(800);
        mEnterBgAnmator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //处理背景，有点透明度
                float value=(float)animation.getAnimatedFraction();
                updateBgAlpha(value);
            }
        });
        //退出
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(800);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float)animation.getAnimatedFraction();
                updateBgAlpha(value);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter=null;
        }


    }

    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                }else{
                    mPlayerPresenter.play();
                }
            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress=progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
mIsUserTouchProgressBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar=false;
                //手离开进度条的时候更新进度
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//todo:播放前一个节目

                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo：播放下一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mTrackPageView.addOnPageChangeListener(this);
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager=true;
                        break;
                }

                return false;
            }
        });
//切换模式的点击事件
        mPlatModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                Switchplaymode();
            }
        });

        mPlayListenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
mSobPopWindows.showAtLocation(v, Gravity.BOTTOM,0,0);
//处理背景的透明度

                mEnterBgAnmator.start();
            }

        });
        mSobPopWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //Pop窗口消失以后
                mOutBgAnimator.start();
            }
        });

        mSobPopWindows.setPlayItemClickListener(new SobPopWindows.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //说明播放列表里的Item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });
        mSobPopWindows.setPlayListActionListener(new SobPopWindows.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                Switchplaymode();
            }

            @Override
            public void onOrderClick() {
                //点击了 切换顺序逆序
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
    }

    private void Switchplaymode() {
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改当前播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
            mCurrentMode= playMode;
        }
    }

    public void updateBgAlpha(float alpha){
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.alpha=alpha;
            window.setAttributes(attributes);
        }

/*
根据当前的状态更新播放模式图标
 */
    private void updatePlayModeBtnImg() {
        int resId=R.drawable.selector_play_mode_list_order;
switch (mCurrentMode){
    case PLAY_MODEL_LIST:
        resId=R.drawable.selector_play_mode_list_order;
        break;
    case PLAY_MODEL_RANDOM:
        resId=R.drawable.selector_paly_mode_random;
        break;
    case PLAY_MODEL_LIST_LOOP:
        resId=R.drawable.selector_paly_mode_list_order_looper;
        break;
    case PLAY_MODEL_SINGLE_LOOP:
        resId=R.drawable.selector_paly_mode_single_loop;
        break;
}
mPlatModeSwitchBtn.setImageResource(resId);

    }

    /*
    找到各个控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackitleText)) {
            mTrackTitleTv.setText(mTrackitleText);
        }
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();

        //设置适配器
mTrackPageView.setAdapter(mTrackPagerAdapter);
        //导入切换播放模式的按钮
        mPlatModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);

        mPlayListenBtn = this.findViewById(R.id.player_list);
        mSobPopWindows = new SobPopWindows();
    }


    @Override
    public void onPlayStart() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop);
        }

    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_palyer_play);
        }

    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_palyer_play);
    }
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
/*
把数据设置到适配器里面
 */

        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
/*
PopView中数据获取回来之后，设置UI
 */
        if (mSobPopWindows != null) {
            mSobPopWindows.setListData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并且修改UI
        mCurrentMode=playMode;
        //更新PopView中的播放模式
        mSobPopWindows.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int  currentDuration, int  total) {
        mDurationBar.setMax(total);
//更新播放进度，更新进度条
        String totalDuration;
        String currentPosition;
        if (total>1000*3600) {
            totalDuration = mHourFormat.format(total);
            currentPosition=mHourFormat.format(currentDuration);
        }else {
            totalDuration=minFormat.format(total);
            currentPosition=minFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
//更新当前时间
            if (mCurrentPosition != null) {
                mCurrentPosition.setText(currentPosition);

        }

//更新进度
        if (!mIsUserTouchProgressBar) {

            mDurationBar.setProgress(currentDuration);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackitleText=track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackitleText);
        }
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex,true);
        }
        if (mSobPopWindows != null) {
            mSobPopWindows.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
mSobPopWindows.updateOrderIcon(isReverse);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

//当页面选中的时候就去播放相应的内容
        if (mPlayerPresenter != null&& mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager=false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
