package com.example.himalaya.Data;

import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {
    private XimalayaApi() {
    }

    private static XimalayaApi sXimalayApi;

    public static XimalayaApi getXimalayaApi() {
        if (sXimalayApi == null) {
            synchronized (XimalayaApi.class) {
                if (sXimalayApi == null) {
                    sXimalayApi = new XimalayaApi();
                }
            }
        }
        return sXimalayApi;
    }
    /*
    获取推荐内容
    callBack：请求返回结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND+"");//RECOMMEND_COUNT表示一页返回多少条数据
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }
    /*
    根据专辑Id获取数据回调，专辑内容
    @param callBack:获取专辑详情的回调接口
    @param albumId：专辑的Id
    @param pageIndex:第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.ALBUM_ID,albumId+ "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map,callBack);
    }
//根据关键字进行搜索
    public void SearchByKeyword(String keyWord, int page, IDataCallBack<SearchAlbumList> callBack) {
        String q = "因为爱情来之不易";
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE,Constants.COUNT_DEFAULT+"");
        CommonRequest.getSearchedAlbums(map,callBack );
    }
    //获取推荐的热词
    public void getHotWord( IDataCallBack<HotWordList> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map, callback);
    }
    //根据关键字获取联想词
    public void getSuggestWord(String keyWord,IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyWord);
        CommonRequest.getSuggestWord(map, callback);
    }
}
