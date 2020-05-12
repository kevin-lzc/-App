package com.example.himalaya.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {
    //格式化时间
    private SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat=new SimpleDateFormat("mm:ss");
    private List<Track> DetailData=new ArrayList<>();
    private ItemClickListener mItemClickListener=null;
    private ItemLongClickListener mItemLongClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);


        return new InnerHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        /*
        设置获取列表点击过后的详情界面
         */
        View itemView=holder.itemView;
        TextView orderTv=itemView.findViewById(R.id.order_text);
        TextView titleTv=itemView.findViewById(R.id.detail_item_title);
        TextView playCount=itemView.findViewById(R.id.detail_item_play_count);
        TextView durationTv=itemView.findViewById(R.id.detail_item_duration);
        TextView updateDateTv=itemView.findViewById(R.id.detail_item_update_time);
        Track track=DetailData.get(position);
        orderTv.setText((position+1)+"");
        titleTv.setText(track.getTrackTitle());
        playCount.setText(track.getPlayCount()+"");
        /*
        播放时间设置
         */
       int durationMil=track.getDuration()*1000;
       String duration=mDurationFormat.format(durationMil);
       durationTv.setText(duration);
        String UpdateTimeText=mSimpleDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(UpdateTimeText);
        /*
        设置itemview的点击事件
         */
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null) {
                    mItemClickListener.onItemClick(DetailData,position);
                }

            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });
    }




    @Override
    public int getItemCount() {
        return DetailData.size();
    }

    public void setData(List<Track> tracks) {
        DetailData.clear();
        DetailData.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener=listener;
    }
    public interface ItemClickListener{
        void onItemClick(List<Track> detailData, int position);
    }
public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener=  listener;
}
    public interface ItemLongClickListener{
void onItemLongClick(Track track);
    }
}
