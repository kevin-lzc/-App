package com.example.himalaya.Adapter;

import android.app.DownloadManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.Innerholder> {
    private List<QueryResult> mData=new ArrayList<>();
    private View mItemView;
    private ItemClickListener mItemClickListener=null;

    @NonNull
    @Override
    public SearchRecommendAdapter.Innerholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tecommend, parent,false);
        return new Innerholder(mItemView);

    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.Innerholder holder, int position) {
 TextView text=holder.itemView.findViewById(R.id.search_recommend_item);
        final QueryResult queryResult = mData.get(position);
        text.setText(queryResult.getKeyword());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<QueryResult> keyWordList) {
mData.clear();
mData.addAll(keyWordList);
notifyDataSetChanged();
    }

    public class Innerholder extends RecyclerView.ViewHolder {
        public Innerholder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener=listener;
    }
    public interface ItemClickListener{
        void onItemClick(String keyword);
    }
}
