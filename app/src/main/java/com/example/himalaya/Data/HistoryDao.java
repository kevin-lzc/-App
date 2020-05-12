package com.example.himalaya.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mDbHelper;
    private IHistoryDaoCallback mCallback = null;
private Object mLock=new Object();
    public HistoryDao() {
        mDbHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {


        synchronized (mLock) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            //添加数据之前先去删除重复的数据
            db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            db.beginTransaction();
            ContentValues values = new ContentValues();


            values.put(Constants.HISTORY_TRACK_ID, track.getDataId());
            values.put(Constants.HISTORY_TITLE, track.getTrackTitle());
            values.put(Constants.HISTORY_COVER, track.getCoverUrlLarge());
            values.put(Constants.HISTORY_PLAY_COUNT, track.getPlayCount());
            values.put(Constants.HISTORY_DURATION, track.getDuration());
            values.put(Constants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
            values.put(Constants.HISTORY_AUTHOR,track.getAnnouncer().getNickname());
            db.insert(Constants.HISTORY_TB_NAME, null, values);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            mCallback.onHistoryAdd(isSuccess);
        }
    }}

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
        SQLiteDatabase db = null;
        boolean isDeleteSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            LogUtil.d(TAG, "delete-->" + delete);
            db.setTransactionSuccessful();
            isDeleteSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDeleteSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryDel(isDeleteSuccess);
            }
        }
    }
    }

    @Override
    public void clearHistory() {
        synchronized (mLock) {
        SQLiteDatabase db = null;
        boolean isDeleteSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME, null, null);
            db.setTransactionSuccessful();
            isDeleteSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDeleteSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoriesClean(isDeleteSuccess);
            }
        }
    }}

    @Override
    public void listHistories() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            //定义集合装查询到的数据
            List<Track> histories = new ArrayList<>();
            try {
                db = mDbHelper.getReadableDatabase();
                db.beginTransaction();
                //查询所有的表中数据
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);
                    String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                db.setTransactionSuccessful();
                //将查询到的结果通知出去
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoriesLoaded(histories);
                }
            }
        }
    }
}