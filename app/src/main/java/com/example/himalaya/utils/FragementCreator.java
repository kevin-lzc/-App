package com.example.himalaya.utils;

import com.example.himalaya.base.Basefragment;
import com.example.himalaya.fragments.HistoryFragement;
import com.example.himalaya.fragments.RecommendFragement;
import com.example.himalaya.fragments.SubscriptionFragement;

import java.util.HashMap;
import java.util.Map;

public class FragementCreator {
    public final  static int INDEX_RECOMMEND=0;
    public final  static int INDEX_HISTORY=2;
    public final  static int INDEX_SUBSCRIPTION=1;
    public static final int Pager_count=3;
private static Map<Integer,Basefragment> sCache=new HashMap<>();
    public static Basefragment getFragement (int index){
Basefragment  basefragment=sCache.get(index);
        if (basefragment!=null) {
            return basefragment;
        }
        switch (index){
            case INDEX_RECOMMEND:
                basefragment=new RecommendFragement();
                break;
            case INDEX_HISTORY:
                basefragment=new HistoryFragement();
                break;
            case INDEX_SUBSCRIPTION:
                basefragment=new SubscriptionFragement();
                break;
        }
        sCache.put(index,basefragment);
        return basefragment;
    }
}
