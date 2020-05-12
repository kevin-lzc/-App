package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.PlayerListAdapter;
import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class SobPopWindows extends PopupWindow {
    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayerListAdapter mPlayerListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClicklIstener=null;
    private View mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopWindows(){
//设置播放列表的宽高
        super( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
 setOutsideTouchable(true);
//载入View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
//设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }



    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        mTrackList = mPopView.findViewById(R.id.play_list_rv);
       //设置布局管理器
        LinearLayoutManager layoutManager=new LinearLayoutManager(BaseApplication.getAppContext());
          mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayerListAdapter = new PlayerListAdapter();
mTrackList.setAdapter(mPlayerListAdapter);
//设置播放模式
        mPlayModeTv = mPopView.findViewById(R.id.play_list_mode_Tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_mode_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }
    private void initEvent() {
        //点击关闭后，窗口就会消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //切换播放模式
        if (mPlayModeClicklIstener != null) {
            mPlayModeClicklIstener.onPlayModeClick();
        }
    }
});
mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //设置PopView中的播放顺序
        if (mPlayModeClicklIstener != null) {
            mPlayModeClicklIstener.onOrderClick();
        }
    }
});
    }
    //设置适配器数据
    public void setListData(List<Track> data){
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setData(data);
        }

    }
    public void setCurrentPlayPosition(int position){
        if (mPlayerListAdapter != null) {
            mPlayerListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }
    public void setPlayItemClickListener(PlayListItemClickListener listener){
        mPlayerListAdapter.setOnItemClickListener(listener);
    }

    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImg(currentMode);
    }
//更新切换顺序的按钮和文字
     public void updateOrderIcon(boolean isOrder){
     mOrderIcon.setImageResource(isOrder? R.drawable.selector_play_mode_list_order:R.drawable.selector_play_mode_list_revers);
        mOrderText.setText( BaseApplication.getAppContext().getResources().getString(isOrder?R.string.order_text:R.string.revers_text));
    }

    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
            int resId=R.drawable.selector_play_mode_list_order;
            int TexId=R.string.play_mode_order_text;
            switch (playMode){
                case PLAY_MODEL_LIST:
                    resId=R.drawable.selector_play_mode_list_order;
                    TexId=R.string.play_mode_order_text;
                    break;
                case PLAY_MODEL_RANDOM:
                    resId=R.drawable.selector_paly_mode_random;
                    TexId=R.string.play_mode_random_text;
                    break;
                case PLAY_MODEL_LIST_LOOP:
                    resId=R.drawable.selector_paly_mode_list_order_looper;
                    TexId=R.string.play_mode_list_play_text;
                    break;
                case PLAY_MODEL_SINGLE_LOOP:
                    resId=R.drawable.selector_paly_mode_single_loop;
                    TexId=R.string.play_mode_single_play_text;
                    break;
            }
        mPlayModeIv.setImageResource(resId);
mPlayModeTv.setText(TexId);




    }

    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }

    public  void setPlayListActionListener(PlayListActionListener playModeListener){
        mPlayModeClicklIstener=playModeListener;
    }

    public interface PlayListActionListener{
//播放模式被点击
        void onPlayModeClick();
        //播放顺序被点击
        void onOrderClick() ;
    }
}
