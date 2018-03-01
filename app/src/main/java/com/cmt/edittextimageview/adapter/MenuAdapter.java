package com.cmt.edittextimageview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmt.edittextimageview.R;
import com.cmt.edittextimageview.entity.MenuItem;

import java.util.List;

/**
 * Created by user on 2016/2/19.
 */
public class MenuAdapter extends BaseAdapter {
    private Context mContext;

    public void setmDatas(List<MenuItem> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    private List<MenuItem> mDatas;
    private LayoutInflater mInflater;

    public MenuAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.left_menu_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.menu_imageview);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.menu_textview);
            viewHolder.menu_size = (TextView) convertView.findViewById(R.id.menu_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MenuItem menuItem = mDatas.get(position);
        if (menuItem.getResId() != 0)
            viewHolder.mImageView.setImageResource(menuItem.getResId());
        viewHolder.mTextView.setText(menuItem.getName());
        viewHolder.menu_size.setText(menuItem.getSize() + "");
        return convertView;
    }

    private static class ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        TextView menu_size;
    }
}
