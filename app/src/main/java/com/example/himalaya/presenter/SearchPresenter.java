package com.example.himalaya.presenter;

import com.example.himalaya.Data.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {
    private static final String TAG = "SearchPresenter";
private List<Album> mSearchResult =new ArrayList<>();
    private List<ISearchCallback> mCallback=new ArrayList<>();

    private String mCurrentKeyword=null;
    private  XimalayaApi mXimalayaApi;
private static final int DEFAULT_PAGE=1;
private int mCurrentPage=DEFAULT_PAGE;
    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();

}
    XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
    private static SearchPresenter sSearchPresenter = null;
    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }
    @Override
    public void doSearch(String keyWord) {
        mCurrentPage=DEFAULT_PAGE;
        mSearchResult.clear();
this.mCurrentKeyword=keyWord;
        search(keyWord);

    }

    private void search(String keyWord) {
        mXimalayaApi.SearchByKeyword(keyWord, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);
                if (albums != null) {
                    LogUtil.d(TAG,""+albums.size());
                    if (isLoadMore) {
                        for (ISearchCallback iSearchCallback : mCallback) {
                            if (albums.size()==0) {
                                iSearchCallback.onLoadedMoreResult(mSearchResult,false);
                            }else {
                                iSearchCallback.onLoadedMoreResult(mSearchResult,true);

                            }
                            iSearchCallback.onLoadedMoreResult(mSearchResult,true);
                        }
                        isLoadMore=false;
                    }else{
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onSearchResultLoaded(mSearchResult);
                    }
                    }
                }else {
                    LogUtil.d(TAG,"ALBUM IS NULL");
                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG,"errorCode"+errorCode);
                LogUtil.d(TAG,"errorMsg"+errorMsg);

                    for (ISearchCallback iSearchCallback : mCallback) {
                        if (isLoadMore) {
                            iSearchCallback.onLoadedMoreResult(mSearchResult,false);
                            mCurrentPage--;
                            isLoadMore=false;
                        }else {
                        iSearchCallback.onError( errorCode,errorMsg);
                }


                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private boolean isLoadMore=false;

    @Override
    public void LoadMore() {
        if(mSearchResult.size()< Constants.COUNT_DEFAULT){
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onLoadedMoreResult(mSearchResult,false);
            }
        }else{
            isLoadMore=true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }

    }

    @Override
    public void getHotWord() {
mXimalayaApi.getHotWord(new IDataCallBack<HotWordList>() {
    @Override
    public void onSuccess(HotWordList hotWordList) {
        if (hotWordList != null) {
            List<HotWord> hotWords = hotWordList.getHotWordList();
            LogUtil.d(TAG,""+hotWords.size());
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onHotWordLoaded(hotWords);
            }
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        LogUtil.d(TAG,"errorCode"+errorCode);
        LogUtil.d(TAG,"errorMsg"+errorMsg);

    }
});
    }

    @Override
    public void getRecommendWord(String KeyWord) {
mXimalayaApi.getSuggestWord(KeyWord, new IDataCallBack<SuggestWords>() {
    @Override
    public void onSuccess(SuggestWords suggestWords) {
        if (suggestWords != null) {
            List<QueryResult> keyWordList = suggestWords.getKeyWordList();
            LogUtil.d(TAG,""+keyWordList.size());
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onRecommendWordLoaded(keyWordList);
            }
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        LogUtil.d(TAG,"errorCode"+errorCode);
        LogUtil.d(TAG,"errorMsg"+errorMsg);
    }
});
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
         mCallback.remove(iSearchCallback);
    }
}
