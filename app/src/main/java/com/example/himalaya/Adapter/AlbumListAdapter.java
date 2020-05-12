package com.example.himalaya.Adapter;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder>{
    private List<Album> mData = new ArrayList<>();
    private onAlbumItemClickListener mItemClickListener=null;
    private onAlbumItemLongClickListener mLongClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里是载View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null) {
                    int clickPosition=(int)v.getTag();
                    mItemClickListener.onItemClick(clickPosition,mData.get(clickPosition));
                }

            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    int clickPosition=(int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                return true;

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;

    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新一下UI。
        notifyDataSetChanged();
    }



    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() + "");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");

            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            }else {
                albumCoverIv.setImageResource(R.mipmap.logo);
            }
        }
    }
    public void setAlbumItemClickListener(onAlbumItemClickListener listener){
        this.mItemClickListener=listener;
    }
    public interface onAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }
    public void setonAlbumItemLongClickListener(onAlbumItemLongClickListener listener){
        this.mLongClickListener=listener;
    }
    /*
    item长按的接口
     */
    public interface onAlbumItemLongClickListener{
        void onItemLongClick(Album album);
    }
}


