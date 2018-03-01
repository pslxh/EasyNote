package com.cmt.edittextimageview.entity;

/**
 * Created by user on 2016/2/19.
 */
public class MenuItem {
    private int resId;
    private String name;

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    private boolean isEncrypt;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private int size;
    public MenuItem(){

    }
    public MenuItem(int resId, String name,int size,boolean isEncrypt) {
        this.resId = resId;
        this.name = name;
        this.size=size;
        this.isEncrypt=isEncrypt;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
