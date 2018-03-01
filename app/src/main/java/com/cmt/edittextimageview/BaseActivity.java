package com.cmt.edittextimageview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmt.edittextimageview.adapter.MenuAdapter;
import com.cmt.edittextimageview.dataHelper.DataBaseManager;
import com.cmt.edittextimageview.entity.MenuItem;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.view.KeyDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.List;

/**
 * Created by admin on 2017/11/21.
 */

public class BaseActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private SlidingMenu slidingMenu;

    private List<MenuItem> mMenuDatas;

    private ListView menu_listview;

    private MenuAdapter menuAdapter;

    private ViewPager myViewPager;

    private TextView munu_typeTextView;

    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.base_activity_main);

        munu_typeTextView = (TextView) findViewById(R.id.munu_type);

        mMenuDatas = DataBaseManager.getInstance(this).queryMunuDatas();

        myViewPager = (ViewPager) findViewById(R.id.myPager);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        myViewPager.setAdapter(pagerAdapter);

        myViewPager.addOnPageChangeListener(this);

        myViewPager.setOffscreenPageLimit(0);

        myViewPager.setCurrentItem(0);

        slidingMenu = (SlidingMenu) findViewById(R.id.slidingmenulayout);

        menu_listview = (ListView) findViewById(R.id.menu_listview);

        menuAdapter = new MenuAdapter(this);

        menu_listview.setAdapter(menuAdapter);

        menuAdapter.setmDatas(mMenuDatas);

        menu_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                myViewPager.setCurrentItem(position);

                slidingMenu.toggle();

                munu_typeTextView.setText(mMenuDatas.get(position).getName());

            }
        });
        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("Adlog", "onPageSelected:" + position);
                if (position == 4) {
                    myViewPager.setVisibility(View.GONE);
                    showDeleteDialog();
                } else {
                    myViewPager.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                mMenuDatas = DataBaseManager.getInstance(getApplicationContext()).queryMunuDatas();
                menuAdapter.setmDatas(mMenuDatas);
                // pagerAdapter.notifyDataSetChanged();
            }
        });
        munu_typeTextView.setText(mMenuDatas.get(0).getName());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mMenuDatas == null ? 0 : mMenuDatas.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return TabFragment.newInstance(mMenuDatas.get(position).getName(), position, mMenuDatas.get(position).getResId(), mMenuDatas.get(position).isEncrypt());
        }
    }
    private void showDeleteDialog() {
        KeyDialog keyDialog = new KeyDialog(this, new KeyDialog.InputKeyCallBack() {
            @Override
            public void inputKeySuccess() {
                myViewPager.setVisibility(View.VISIBLE);
            }
        });
        keyDialog.show();
    }

}
