package com.cmt.edittextimageview.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;

/**
 * Created by xlc on 2016/12/15.
 */
public class Util {

    public static final String ENCRYPT_KEY = "encrypt_tag";

    private static final String EASY_NOTE_XML = "easyNote_xml";

    /**
     * 时间戳转日期
     **/
    public static String toDateFormat(long time) {

        SimpleDateFormat format;

        long sum = Math.abs(System.currentTimeMillis() - time);

        long aDay = 24 * 60 * 60000;

        if (sum < aDay) {
            format = new SimpleDateFormat("HH:mm");
        } else {
            format = new SimpleDateFormat("MM-dd");
        }
        String d = format.format(time);
        return d;
    }
    public static void save_keyword(Context context, String keyword) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(EASY_NOTE_XML, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ENCRYPT_KEY, keyword);
        editor.apply();
    }
    public static String get_keyword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(EASY_NOTE_XML, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ENCRYPT_KEY, null);
    }

}
