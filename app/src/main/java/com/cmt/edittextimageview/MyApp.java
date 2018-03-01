package com.cmt.edittextimageview;

import android.app.Application;
import android.content.Context;

import com.cmt.edittextimageview.dataHelper.DataBaseManager;
import com.cmt.edittextimageview.entity.MenuItem;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by xlc on 2016/12/14.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        init();
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void init() {
        Random random = new Random();
        int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MenuItem> mMenuDatas = new ArrayList<>();
                mMenuDatas.add(new MenuItem(R.color.trans, getResources().getString(R.string.menu_all), 0, false));
                mMenuDatas.add(new MenuItem(R.color.green, getResources().getString(R.string.menu_work), 0, false));
                mMenuDatas.add(new MenuItem(R.color.qin_se, getResources().getString(R.string.menu_live), 0, false));
                mMenuDatas.add(new MenuItem(R.color.zi_se, getResources().getString(R.string.menu_temporary), 0, false));
                mMenuDatas.add(new MenuItem(R.drawable.lock, getResources().getString(R.string.menu_encryption), 0, true));
                for (MenuItem menuItem : mMenuDatas) {
                    DataBaseManager.getInstance(getApplicationContext()).inserMunuDate(menuItem);
                }
            }
        }).start();
    }
}
