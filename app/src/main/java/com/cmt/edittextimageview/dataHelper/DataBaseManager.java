package com.cmt.edittextimageview.dataHelper;

import android.content.Context;


import com.cmt.edittextimageview.entity.MenuItem;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.listener.SaveCompletedListener;

import java.util.List;

/**
 * Created by aspsine on 15-4-19.
 */
public class DataBaseManager {
    private static DataBaseManager sDataBaseManager;
    private final ThreadInfoDao mThreadInfoDao;

    public static DataBaseManager getInstance(Context context) {
        if (sDataBaseManager == null) {
            sDataBaseManager = new DataBaseManager(context);
        }
        return sDataBaseManager;
    }

    private DataBaseManager(Context context) {
        mThreadInfoDao = new ThreadInfoDao(context);
    }


    public synchronized void insertData(Note note,Context context) {
        mThreadInfoDao.addNote(note,context);
    }

    public synchronized void updata( Note note) {
        mThreadInfoDao.updateData(note);
    }

    public synchronized  void  updateAudiopath(String path,int id){

        mThreadInfoDao.updateAudioPath(path,id);
    }

    public synchronized List<Note> qurydata() {
        return mThreadInfoDao.getAllNote();
    }

    public synchronized List<Note> queryDatasByTypeName(String t_name) {

        return mThreadInfoDao.getNoteByTyeName(t_name);
    }

    public synchronized void setListener(SaveCompletedListener listener){

        mThreadInfoDao.setCompletedListener(listener);
    }

    public synchronized  void inserMunuDate(MenuItem menuItem){

        mThreadInfoDao.inserMunuData(menuItem);
    }

    public List<MenuItem> queryMunuDatas(){

        return mThreadInfoDao.queryMenuDatas();
    }

    public int getResourceByTypeName(String type_name){

        return mThreadInfoDao.queryResourceByName(type_name);
    }

    public void deleteData(Context context,Note note){

        mThreadInfoDao.deleteNote(note,context);
    }

}
