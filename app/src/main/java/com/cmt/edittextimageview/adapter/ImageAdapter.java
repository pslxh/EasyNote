package com.cmt.edittextimageview.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.cmt.edittextimageview.R;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.example.EditorActivity;
import com.cmt.edittextimageview.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Ivor on 2016/2/6.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context mContext;
    private List<Note> notes;
    private List<Integer> heights;
    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
        this.notes = new ArrayList<>();
        setHasStableIds(true);
    }
    public void setData(List<Note> l) {

        this.notes = new ArrayList<>();

        this.notes.addAll(l);

        this.notifyDataSetChanged();
    }

    public void addData(List<Note> l) {

        this.notes.addAll(l);

        notifyDataSetChanged();
    }

    private void getRandomHeight(List<String> lists) {//得到随机item的高度

        heights = new ArrayList<>();

        for (int i = 0; i < lists.size(); i++) {

            heights.add((int) (200 + Math.random() * 400));
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Note note = notes.get(position);

        holder.create_note_time.setText(String.format(mContext.getResources().getString(R.string.adapter_time), Util.toDateFormat(note.getAdd_time())));

        holder.adapter_type_name.setText(note.getType_name());

        holder.adapter_type_resource.setImageResource(note.getResourceId());

        holder.content.setText(sub_imgPath(note.getContent()));

        if (TextUtils.isEmpty(note.getTitle())) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(note.getTitle());
        }
        String img_path = note.getImgPath();
        holder.audio_img.setVisibility(TextUtils.isEmpty(note.getAudio_path()) ? View.GONE : View.VISIBLE);
        if (TextUtils.isEmpty(img_path) || !checkImage(img_path)) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            ImageLoader.getInstance().displayImage(img_path, holder.imageView);
            Log.e("Adlog", "\"file://\"+image.getImgPath():" + note.getImgPath());
        }
    }

    private boolean checkImage(String path){

        return path.endsWith(".png")||path.endsWith(".jpg");
    }

    private String sub_imgPath(String msg) {
        String reg = "<img.*?>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            //继续查找下一个匹配对象
            Log.e("Alog", "img标签===》" + matcher.group());
            msg = msg.replace(matcher.group(), "");
        }
        msg = msg.replace("\n", "");
        return msg;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return notes.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, adapter_type_resource, audio_img;

        TextView content, title, create_note_time, adapter_type_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.note_imageView);
            content = (TextView) itemView.findViewById(R.id.note_content);
            title = (TextView) itemView.findViewById(R.id.note_title);
            create_note_time = (TextView) itemView.findViewById(R.id.create_note_time);
            adapter_type_resource = (ImageView) itemView.findViewById(R.id.adapter_type_resource);
            adapter_type_name = (TextView) itemView.findViewById(R.id.adapter_type_name);
            audio_img = (ImageView) itemView.findViewById(R.id.audio_img);
        }
    }

}
