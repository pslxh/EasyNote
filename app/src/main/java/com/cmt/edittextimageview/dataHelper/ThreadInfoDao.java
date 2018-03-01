package com.cmt.edittextimageview.dataHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.cmt.edittextimageview.R;
import com.cmt.edittextimageview.entity.MenuItem;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.listener.SaveCompletedListener;
import com.cmt.edittextimageview.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aspsine on 15-4-19.
 */
public class ThreadInfoDao extends AbstractDao {

    public static final String ALL_TEXT = "all_text";

    //侧边tab表
    public static final String MUNT_TAB = "m_tab";


    public ThreadInfoDao(Context context) {
        super(context);
    }

    public static void createTable(SQLiteDatabase db) {

        db.execSQL("create table " + ALL_TEXT + "(_id integer primary key autoincrement,title text,audio_path text,img_path text, content text,type integer,add_time integer,type_name text,resource_id integer)");

        db.execSQL("create table " + MUNT_TAB + "(_id integer primary key autoincrement,type_size integer,type_name text,type_source integer)");

    }

    public static void dropTable(SQLiteDatabase db) {

        db.execSQL("drop table if exists " + ALL_TEXT);

        db.execSQL("drop table if exists " + MUNT_TAB);
    }


    public void setCompletedListener(SaveCompletedListener completedListener) {
        this.completedListener = completedListener;
    }

    private SaveCompletedListener completedListener;

    /**
     * 删除数据
     *
     * @param info
     */
    public void deleteNote(Note info, Context context) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();

            db.execSQL("delete from " + ALL_TEXT + " where _id = ? ", new Object[]{info.get_id()});

            updateMunuData(info.getType_name(), false, context);

            if(completedListener!=null){
                completedListener.saveDataComplete();
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Log.e("Adlog", " 删除 error " + e.getMessage());

        } finally {
            db.endTransaction();
        }
    }

    /**
     * 添加
     *
     * @param info
     */
    public void addNote(Note info, Context context) {

        SQLiteDatabase db = getWritableDatabase();

        try {

            db.beginTransaction();

            db.insert(ALL_TEXT, null, info.toContentValus());

            updateMunuData(info.getType_name(), true, context);

            if (completedListener != null) {
                completedListener.saveDataComplete();
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Log.e("Adlog", " Caching insert error " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    public List<Note> getAllNote() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;

        List<Note> notes = new ArrayList<>();

        try {
            cursor = db.rawQuery("select * from " + ALL_TEXT, null);
            while (cursor.moveToNext()) {
                notes.add(getNote(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return notes;
    }

    private Note getNote(Cursor cursor) {
        Note note = new Note();
        note.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
        note.setContent(cursor.getString(cursor.getColumnIndex("content")));
        note.setType(cursor.getInt(cursor.getColumnIndex("type")));
        note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        note.setImgPath(cursor.getString(cursor.getColumnIndex("img_path")));
        note.setAdd_time(cursor.getLong(cursor.getColumnIndex("add_time")));
        note.setType_name(cursor.getString(cursor.getColumnIndex("type_name")));
        note.setResourceId(cursor.getInt(cursor.getColumnIndex("resource_id")));
        note.setAudio_path(cursor.getString(cursor.getColumnIndex("audio_path")));
        Log.e("Adlog", "time:" + Util.toDateFormat(cursor.getLong(cursor.getColumnIndex("add_time"))));
        return note;
    }

    public List<Note> getNoteByTyeName(String type_name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        List<Note> notes = new ArrayList<>();
        try {
            cursor = db.rawQuery("select * from " + ALL_TEXT + " where type_name=?", new String[]{type_name});
            while (cursor.moveToNext()) {
                notes.add(getNote(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return notes;
    }

    /**
     * 修改数据
     *
     * @param note
     */
    public void updateData(Note note) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.update(ALL_TEXT, note.toContentValus(), "_id=" + note.get_id(), null);
            if (completedListener != null) {
                completedListener.saveDataComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAudioPath(String path, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update " + ALL_TEXT + " set audio_path=? where _id=?", new Object[]{path, id});
        if (completedListener != null) {
            completedListener.saveDataComplete();
        }
    }
    /**
     * 删除数据
     */
//    public void delete_favorite(String url) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("delete from "
//                        + FAVORITE
//                        + " where url = ? ",
//                new Object[]{url});
//    }
    /***
     * 查询侧标列数据
     * @return
     */
    public List<MenuItem> queryMenuDatas() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;

        List<MenuItem> menuItems = new ArrayList<>();

        try {
            cursor = db.rawQuery("select * from " + MUNT_TAB, null);

            while (cursor.moveToNext()) {

                MenuItem menuItem = new MenuItem();

                menuItem.setName(cursor.getString(cursor.getColumnIndex("type_name")));

                menuItem.setSize(cursor.getInt(cursor.getColumnIndex("type_size")));

                menuItem.setResId(cursor.getInt(cursor.getColumnIndex("type_source")));

                menuItems.add(menuItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return menuItems;
    }


    public int queryResourceByName(String name) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from " + MUNT_TAB, new String[]{name});

            if (cursor.moveToNext()) {

                return cursor.getInt(cursor.getColumnIndex("type_source"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return 0;
    }

    /**
     * 插入数据
     *
     * @param menuItem
     */
    public void inserMunuData(MenuItem menuItem) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from " + MUNT_TAB + " where type_name=?", new String[]{menuItem.getName()});

            if (cursor.moveToNext()) {

                Log.e("AeLog", "存在不做插入");

                return;
            }
            ContentValues contentValues = new ContentValues();

            contentValues.put("type_name", menuItem.getName());

            contentValues.put("type_size", menuItem.getSize());

            contentValues.put("type_source", menuItem.getResId());

            db.insert(MUNT_TAB, null, contentValues);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateMunuData(String type_name, boolean up_down, Context context) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            String sql;
            if (up_down) {
                sql = "update " + MUNT_TAB + " set type_size=type_size+1 where type_name =? or type_name=?";
            } else {
                sql = "update " + MUNT_TAB + " set type_size=type_size-1 where type_name =? or type_name=?";
            }
            db.execSQL(sql, new String[]{type_name, context.getResources().getString(R.string.menu_all)});

        } catch (Exception e) {
            Log.e("AeLog", "更新错误：" + e.getMessage());
            e.printStackTrace();
        }
    }


}
