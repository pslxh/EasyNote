package com.cmt.edittextimageview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cmt.edittextimageview.adapter.ImageAdapter;
import com.cmt.edittextimageview.dataHelper.DataBaseManager;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.example.EditorActivity;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    private ImageView add_note_btn;

    private ImageView back_btn;

    private ImageAdapter adapter;

    private StaggeredGridLayoutManager mStaggeredLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        add_note_btn = (ImageView) findViewById(R.id.add_note);

        add_note_btn.setOnClickListener(this);

        back_btn = (ImageView) findViewById(R.id.back);

        back_btn.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.listView);

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mStaggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        //设置item之间的间隔
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);

        mRecyclerView.addItemDecoration(decoration);

        adapter = new ImageAdapter(this);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
    }
    @Override
    protected void onResume() {
        super.onResume();

        List<Note> list = DataBaseManager.getInstance(this).qurydata();

        Log.e("Adlog", "list.size:" + list.size());

        adapter.setData(list);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_note:
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
                break;
        }

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


}
