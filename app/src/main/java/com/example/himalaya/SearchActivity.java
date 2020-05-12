package com.example.himalaya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.Adapter.AlbumListAdapter;
import com.example.himalaya.Adapter.SearchRecommendAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenter.AlbumDetailPresenter;
import com.example.himalaya.presenter.SearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.DetailActivity;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback, AlbumListAdapter.onAlbumItemClickListener {

    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;

    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mImm;
    private View mDelBtn;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSuggestWords=true;

    //    private FlowTextLayout mFlowTextLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        //注册UI更新的接口
        mImm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }


    private void initView() {


        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mDelBtn = this.findViewById(R.id.search_input_delete);
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
mImm.showSoftInput(mInputBox,InputMethodManager.SHOW_IMPLICIT);
            }
        },100);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return CreateSuccessView();
                }
                @Override
                protected View getEmpty() {
                    //创建一个新的
                    View emptyView= LayoutInflater.from(getContext()).inflate(R.layout.fragement_empty_view,this,false);
                    TextView tipsView=emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.search_no_content_tips_text);
                    return emptyView;
                }
            };
            if (mUILoader.getParent()instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);
        }


//        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
    }
//创建一个数据请求成功的View
    private View CreateSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        //刷新控件
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mRefreshLayout.setEnableRefresh(false);
       // mRefreshLayout.setOverScrollBottomShow(false);
        //显示热词
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        mResultListView = resultView.findViewById(R.id.result_list_view);
//设置布局管理器
        LinearLayoutManager ResultLayoutManager=new LinearLayoutManager(this);
        mResultListView.setLayoutManager(ResultLayoutManager);
//设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理器
        LinearLayoutManager recommendLayoutManager=new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(recommendLayoutManager);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {//这个方法用于设置ItemView的间距
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),2);
                outRect.bottom= UIUtil.dip2px(view.getContext(),2);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //设置适配器
        mRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mRecommendAdapter);

        return resultView;



    }

    @Override
    protected void onDestroy() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
            }
        });
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter=null;
        }
    }

    private void initEvent() {
        mAlbumListAdapter.setAlbumItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //搜索框加载更多
                    if (mSearchPresenter != null) {
                        mSearchPresenter.LoadMore();
                    }
            }
        });
        if (mRecommendAdapter != null) {
            mRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    //不需要相关热词的联想
                    mNeedSuggestWords=false;
//执行搜索框弹出的推荐词搜索
                    Switch2Search(keyword);
                }
            });
        }
        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击清空输入框内容
                mInputBox.setText("");
            }
        });
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要相关热词的联想
                mNeedSuggestWords=false;
                Switch2Search(text);
            }
        });
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }

        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行搜索
                String Keyword = mInputBox.getText().toString().trim();
                if (TextUtils.isEmpty(Keyword)) {
                    Toast.makeText(SearchActivity.this,"搜索关键字不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(Keyword);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
@Override
public void beforeTextChanged(CharSequence s, int start, int count, int after) {

}

 @Override
public void onTextChanged(CharSequence s, int start, int before, int count) {
     if (TextUtils.isEmpty(s)) {
         mSearchPresenter.getHotWord();
         //触发联想查询

     }else {
         mDelBtn.setVisibility(View.VISIBLE);
         if (mNeedSuggestWords) {
             getSuggestWord(s.toString());
         }else {
             mNeedSuggestWords=true;
         }
     }
}

@Override
public void afterTextChanged(Editable s) {

 }
      }
        );
//        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
//            @Override
//            public void onItemClick(String text) {
//                Toast.makeText(SearchActivity.this,text,Toast.LENGTH_SHORT).show();
//            }
//        });
   }

    private void Switch2Search(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(SearchActivity.this,"搜索关键字不能为空",Toast.LENGTH_SHORT).show();
        }
        //把热词扔到输入框里面
        mInputBox.setText(text);
        mInputBox.setSelection(text.length());
        //发起搜索
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(text);
        }
        //改变UI状态
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    private void getSuggestWord(String keyWord) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyWord);
        }
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        HandleSearchResult(result);
        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void HandleSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size()==0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            }else {
                //如果数据不为空就设置数据
                mAlbumListAdapter.setData(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG,""+hotWordList.size());
        List<String> hotWords=new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchword = hotWord.getSearchword();
            hotWords.add(searchword);
        }
        Collections.sort(hotWords);
        //更新UI
     mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadedMoreResult(List<Album> result, boolean isOkay) {
//处理加载更多的结果
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        if (isOkay) {
            HandleSearchResult(result);
        }else {
Toast.makeText(SearchActivity.this,"没有更多内容",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
//数据请求回来之后的，联想热词
        LogUtil.d(TAG,"---->"+keyWordList.size());
        if (mRecommendAdapter != null) {
            mRecommendAdapter.setData(keyWordList);

        }
        //控制UI的状态和隐藏显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和隐藏
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

        }
    }
    private void hideSuccessView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        Intent intent=new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}