package com.cmt.edittextimageview.entity;

import android.content.ContentValues;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by xlc on 2016/12/14.
 */
public class Note implements Serializable {

    public static final String _ID = "_id";

    public static final String CONTENT = "content";

    public static final String TITLE = "title";

    public static final String TYPE = "type";

    public static final String IMGPATH = "img_path";

    public static final String ADD_TIME = "add_time";

    public static final String TYPENAME = "type_name";

    public static final String RESOURCEID = "resource_id";

    public static final String AUDIO_PATH = "audio_path";

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }

    private String audio_path;


    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    private int resourceId;

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    private String type_name;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    private int _id;

    private int type;

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    private long add_time;

    private String title;

    private String imgPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public ContentValues toContentValus() {

        ContentValues cv = new ContentValues();

        cv.put(CONTENT, content);

        cv.put(TITLE, title);

        cv.put(TYPE, type);

        cv.put(ADD_TIME, add_time);

        cv.put(TYPENAME, type_name);

        cv.put(RESOURCEID, resourceId);

        cv.put(IMGPATH, imgPath);

        cv.put(AUDIO_PATH, audio_path);

        return cv;

    }

}
