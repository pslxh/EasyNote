package com.cmt.edittextimageview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cmt.edittextimageview.adapter.ImageAdapter;
import com.cmt.edittextimageview.dataHelper.DataBaseManager;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.example.EditorActivity;
import com.cmt.edittextimageview.listener.SaveCompletedListener;
import com.cmt.edittextimageview.util.TypeConfigStatus;
import com.cmt.edittextimageview.view.KeyDialog;

import java.util.List;


/**
 * Created by xlc on 2016/9/27.
 */
public class TabFragment extends Fragment implements View.OnClickListener, SaveCompletedListener {
    /****
     * viewpager位置标识
     ***/
    private static final String TYPE_NAME = "type_name";
    /******
     * 链接类型标识
     *****/
    private static final String POSITION = "t_position";

    private static final String TYPE_RESOURCE = "type_resource";

    private static final String TYPE_ISENCRYPT = "isEncrypt";

    private String type_name;

    private int position;

    private int type_resource;

    private boolean isEncrypt;

    private RecyclerView mRecyclerView;

    private LinearLayout note_list_layout;

    private ImageView add_note_btn;

    private ImageView back_btn;

    private ImageAdapter adapter;

    private StaggeredGridLayoutManager mStaggeredLayoutManager;

    public static TabFragment newInstance(String type_name, int postion, int res, boolean isEncrypt) {
        TabFragment f = new TabFragment();
        Bundle b = new Bundle();
        b.putString(TYPE_NAME, type_name);
        b.putInt(POSITION, postion);
        b.putInt(TYPE_RESOURCE, res);
        b.putBoolean(TYPE_ISENCRYPT, isEncrypt);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(POSITION);
        type_name = getArguments().getString(TYPE_NAME);
        type_resource = getArguments().getInt(TYPE_RESOURCE);
        isEncrypt = getArguments().getBoolean(TYPE_ISENCRYPT);
        // T t = (T) getArguments().getSerializable(T_OBJECT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);

        note_list_layout = (LinearLayout) view.findViewById(R.id.note_list_layout);

        DataBaseManager.getInstance(getActivity()).setListener(this);

        add_note_btn = (ImageView) view.findViewById(R.id.add_note);

        add_note_btn.setOnClickListener(this);

        back_btn = (ImageView) view.findViewById(R.id.back);

        back_btn.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.listView);

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mStaggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        //设置item之间的间隔
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);

        mRecyclerView.addItemDecoration(decoration);

        adapter = new ImageAdapter(getActivity());

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (mGestureDetector.onTouchEvent(e)) {
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
        return view;
    }

    //长按事件
    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(int position, View childView) {
            Toast.makeText(getActivity(), "长按:" + position, Toast.LENGTH_SHORT).show();

            showDeleteDialog(list.get(position));
        }
    };
    //单击事件
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int p, View childView) {
            final Note note = list.get(p);
            if (note.getType() == TypeConfigStatus.TYPE_KEY_WORD && position != TypeConfigStatus.TYPE_KEY_WORD) {
                KeyDialog keyDialog = new KeyDialog(getActivity(), new KeyDialog.InputKeyCallBack() {
                    @Override
                    public void inputKeySuccess() {
                        startEditActivity(note);
                    }
                });
                keyDialog.show();
            } else {
                startEditActivity(note);
            }
        }
    };

    private void startEditActivity(Note note) {
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable("notes", note);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    List<Note> list;

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Adlog", "position:" + position);
        initDatas();
        if (isEncrypt) {
            note_list_layout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        if (position == 0) {
            list = DataBaseManager.getInstance(getActivity()).qurydata();
        } else {
            list = DataBaseManager.getInstance(getActivity()).queryDatasByTypeName(type_name);
        }
        Log.e("Adlog", "list.size:" + list.size());
        adapter.setData(list);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.add_note:

                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("type_name", type_name);
                bundle.putSerializable("type_resource", type_resource);
                bundle.putInt("type", position);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);

                break;
        }
    }

    @Override
    public void saveDataComplete() {
        initDatas();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            // if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
            //  }
        }
    }


    //以下是添加点击、长按事件的关键代码
    private GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
        //长按事件
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mOnItemLongClickListener != null) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int position = mRecyclerView.getChildLayoutPosition(childView);
                    mOnItemLongClickListener.onItemLongClick(position, childView);
                }
            }
        }

        //单击事件
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnItemClickListener != null) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null) {
                    int position = mRecyclerView.getChildLayoutPosition(childView);
                    mOnItemClickListener.onItemClick(position, childView);
                    return true;
                }
            }

            return super.onSingleTapUp(e);
        }
    });


    //长按事件接口
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, View childView);
    }

    //单击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position, View childView);
    }


    private void showDeleteDialog(final Note note) {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.drawable.logo_note);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        DataBaseManager.getInstance(getActivity()).deleteData(getActivity(), note);
                        //刷新数据
                        initDatas();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


}
