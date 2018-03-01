package com.cmt.edittextimageview.example;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmt.edittextimageview.MainActivity;
import com.cmt.edittextimageview.PlayActivity;
import com.cmt.edittextimageview.R;
import com.cmt.edittextimageview.audio.AudioRecoderUtils;
import com.cmt.edittextimageview.audio.PopupWindowFactory;
import com.cmt.edittextimageview.audio.TimeUtils;
import com.cmt.edittextimageview.dataHelper.DataBaseManager;
import com.cmt.edittextimageview.entity.Note;
import com.cmt.edittextimageview.util.PerformEdit;
import com.cmt.edittextimageview.util.Util;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author HUST_LH
 */
@SuppressLint("HandlerLeak")
public class EditorActivity extends Activity implements OnClickListener {

    private ResizeLinearLayout baseContent;

    private ImageView ed_type_source;

    private TextView ed_type_name;

    private EditText articleTitle;

    private RichEditText contentRichEditText;

    private ImageView completeImg;

    private ImageView galleryImg;

    private ImageView note_back_btn;

    private ImageView note_audio;

    private ImageView undo_btn;

    private ImageView redo_btn;

    private PerformEdit mPerformEdit;

    private String imgPath;

    private Note intent_note = null;

    //类型
    private String typeName;

    //修改时间
    private long write_time;

    private TextView ed_write_time;

    private int type_resource;

    private int type_status;

    private int appHeight;

    private int baseLayoutHeight;

    private int currentStatus;
    private static final int SHOW_TOOLS = 1;
    private static final int SHOW_KEY_BOARD = 2;
    private static final int RESIZE_LAYOUT = 1;

    private boolean flag = false; // 控制何时显示下方tools

    private InputHandler inputHandler = new InputHandler();

    private ImageView mImageView;
    private TextView mTextView;
    private AudioRecoderUtils mAudioRecoderUtils;
    private Context context;
    private PopupWindowFactory mPop;
    private RelativeLayout rl;

    private LinearLayout cancleAudio, okAudio;

    private String audioPath;

    private ImageView deleteAudio, playAudio;

    private LinearLayout audio_layout;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.editor_gallery_img:
                gallery();
                break;
            case R.id.note_finish:

                Log.e("Adlog", "点击保存");

                String content = contentRichEditText.getText().toString();

                if (TextUtils.isEmpty(content)) return;

                Note note = new Note();

                note.setType(type_status);

                note.setContent(content);

                note.setResourceId(type_resource);

                note.setType_name(typeName);

                note.setAdd_time(System.currentTimeMillis());

                note.setTitle(articleTitle.getText().toString());

                Log.e("Adlog", "图片地址：" + sub_imgPath(content));

                note.setImgPath(sub_imgPath(content));

                Log.e("Adlog", "录音地址：" + audioPath);

                note.setAudio_path(audioPath);

                if (intent_note != null) {

                    note.set_id(intent_note.get_id());

                    DataBaseManager.getInstance(getApplicationContext()).updata(note);

                } else {
                    DataBaseManager.getInstance(getApplicationContext()).insertData(note, getApplicationContext());
                }
                finish();
                break;
            case R.id.note_back:
                finish();
                break;

            case R.id.note_redo:

                mPerformEdit.redo();

                break;

            case R.id.note_undo:

                mPerformEdit.undo();

                break;

            case R.id.note_audio:

                mPop.showAtLocation(baseContent, Gravity.CENTER, 0, 0);

                setBackgroundAlpha(0.2f);

                mAudioRecoderUtils.startRecord();

                break;
            case R.id.ok_:

                mAudioRecoderUtils.stopRecord();

                mPop.dismiss();

                audio_layout.setVisibility(View.VISIBLE);

                break;

            case R.id.cancel_:

                mAudioRecoderUtils.cancelRecord();

                mPop.dismiss();

                break;

            case R.id.play_audio_btn:

                Intent intent = new Intent(EditorActivity.this, PlayActivity.class);

                intent.putExtra("audio_path", audioPath);

                startActivity(intent);

                break;

            case R.id.delete_audio_btn:

                showDeleteDialog();

                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDeleteDialog() {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(EditorActivity.this);
        normalDialog.setIcon(R.drawable.logo_note);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("确定要删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File file = new File(audioPath);

                        if (file.exists()) file.delete();

                        audioPath = null;

                        DataBaseManager.getInstance(EditorActivity.this).updateAudiopath(null, intent_note.get_id());

                        audio_layout.setVisibility(View.GONE);
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


    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }


    private String sub_imgPath(String msg) {

        String str = "<img.*?>";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {

            String matchString = matcher.group();

            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(matchString);

            while (m.find()) {

                msg = m.group(1);

                break;
            }
        }
        return msg;
    }

    private class InputHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case RESIZE_LAYOUT:
                    if (msg.arg1 == SHOW_TOOLS) {
                        currentStatus = SHOW_TOOLS;
                    } else {
                        currentStatus = SHOW_KEY_BOARD;
                        baseLayoutHeight = baseContent.getHeight();
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        audio_layout = (LinearLayout) findViewById(R.id.audio_layout);
        deleteAudio = (ImageView) findViewById(R.id.delete_audio_btn);
        playAudio = (ImageView) findViewById(R.id.play_audio_btn);
        deleteAudio.setOnClickListener(this);
        playAudio.setOnClickListener(this);
        //PopupWindow的布局文件
        final View view = View.inflate(this, R.layout.audio_pop_layout, null);
        cancleAudio = (LinearLayout) view.findViewById(R.id.cancel_);
        okAudio = (LinearLayout) view.findViewById(R.id.ok_);
        cancleAudio.setOnClickListener(this);
        okAudio.setOnClickListener(this);
        mPop = new PopupWindowFactory(this, view);
        mPop.setDissListener(new PopupWindowFactory.DissListener() {
            @Override
            public void disCallback() {
                setBackgroundAlpha(1);
            }
        });
        //PopupWindow布局文件里面的控件
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);

        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);

        mAudioRecoderUtils = new AudioRecoderUtils();

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {

                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));

                mTextView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {

                audioPath = filePath;

                Toast.makeText(EditorActivity.this, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();

                mTextView.setText(TimeUtils.long2String(0));
            }
        });
        note_audio = (ImageView) findViewById(R.id.note_audio);
        note_audio.setOnClickListener(this);
        baseContent = (ResizeLinearLayout) findViewById(R.id.editor_base_content);
        completeImg = (ImageView) findViewById(R.id.note_finish);
        galleryImg = (ImageView) findViewById(R.id.editor_gallery_img);
        articleTitle = (EditText) findViewById(R.id.editor_article_title);
        note_back_btn = (ImageView) findViewById(R.id.note_back);
        undo_btn = (ImageView) findViewById(R.id.note_undo);
        redo_btn = (ImageView) findViewById(R.id.note_redo);
        undo_btn.setOnClickListener(this);
        redo_btn.setOnClickListener(this);
        completeImg.setOnClickListener(this);
        galleryImg.setOnClickListener(this);
        note_back_btn.setOnClickListener(this);
        contentRichEditText = (RichEditText) findViewById(R.id.editor_edit_area);
        appHeight = getAppHeight();
        ed_type_name = (TextView) findViewById(R.id.ed_type_name);
        ed_type_source = (ImageView) findViewById(R.id.ed_type_source);
        ed_write_time = (TextView) findViewById(R.id.ed_write_time);
        initImageLoader(this);
        init();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            typeName = b.getString("type_name");
            type_resource = b.getInt("type_resource");
            intent_note = (Note) b.getSerializable("notes");
            type_status=b.getInt("type");
            if (intent_note != null) {
                typeName = intent_note.getType_name();
                write_time = intent_note.getAdd_time();
                type_resource = intent_note.getResourceId();
                Log.e("Adlog", "内容：" + intent_note.getContent());
                contentRichEditText.setRichText(intent_note.getContent());
                articleTitle.setText(intent_note.getTitle());
                audioPath = intent_note.getAudio_path();
                if (!TextUtils.isEmpty(audioPath)) {
                    audio_layout.setVisibility(View.VISIBLE);
                } else {
                    audio_layout.setVisibility(View.GONE);
                }
            }
        }
        ed_type_source.setImageResource(type_resource);
        ed_type_name.setText(typeName);
        if (write_time == 0) {
            ed_write_time.setText(String.format(getResources().getString(R.string.create_write_time), Util.toDateFormat(System.currentTimeMillis())));
        } else {
            ed_write_time.setText(String.format(getResources().getString(R.string.creat_note_time), Util.toDateFormat(write_time)));
        }
        mPerformEdit = new PerformEdit(contentRichEditText, intent_note == null ? "" : intent_note.getContent()) {
            @Override
            protected void canUndo(boolean can) {
                super.canUndo(can);
                if (can) {
                    undo_btn.setImageResource(R.drawable.note_undo_can);
                } else {
                    undo_btn.setImageResource(R.drawable.note_undo_no);
                }
            }

            @Override
            protected void canRedo(boolean can) {
                super.canRedo(can);

                if (can) {
                    redo_btn.setImageResource(R.drawable.note_redo_can);
                } else {
                    redo_btn.setImageResource(R.drawable.note_redo_no);
                }
            }
        };


        contentRichEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String src = contentRichEditText.getText().toString();

                Log.e("Adlog", "aaa:" + src);

                if (TextUtils.isEmpty(src) || !src.contains("<img")) return;

                String twoString = src.substring(contentRichEditText.getSelectionStart() - 2, contentRichEditText.getSelectionStart());

                if (twoString.equals("/>")) {

                    Log.e("Adlog", "aaaaaaaaa:" + contentRichEditText.getSelectionStart());

                    int startIndex = getIndexString(src, contentRichEditText.getSelectionStart());

                    Selection.setSelection(contentRichEditText.getText(), startIndex, contentRichEditText.getSelectionStart());

                }

            }
        });

    }

    /***
     *
     * @param res
     * @return
     */
    private int getIndexString(String res, int edIndex) {

        List<Integer> list = new ArrayList<>();

        int sum;
        int sumMin = 10000;
        int returnIndex = -1;
        while (res.contains("<img")) {
            int index = res.indexOf("<img");
            list.add(index);
            res = res.replaceFirst("<img", "aaa");
        }
        if (list.size() <= 0) return -1;
        for (int i = 0; i < list.size(); i++) {
            sum = edIndex - list.get(i);
            if (sum < sumMin && edIndex > list.get(i)) {
                sumMin = sum;
                returnIndex = list.get(i);
            }
        }
        return returnIndex;

    }


    private void init() {
        baseContent.setOnResizeListener(new ResizeLinearLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                // TODO Auto-generated method stub
                int selector = SHOW_TOOLS;
                if (h < oldh) {
                    selector = SHOW_KEY_BOARD;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = selector;
                inputHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取应用显示区域高度。。。 PS:该方法放到工具类使用会报NPE ，怀疑是没有传入activity所致，没有深究
     *
     * @return
     */
    public int getAppHeight() {
        /**
         * 获取屏幕物理尺寸高(单位：px)
         */
        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        /**
         * 获取设备状态栏高度
         */
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, top = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            top = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        /**
         * 屏幕高度减去状态栏高度即为应用显示区域高度
         */
        return ds.heightPixels - top;
    }

    /**
     * 系统软键盘与工具栏的切换显示
     */
    private void showTools(int id) {
        if (id == R.id.editor_gallery_img) {
            flag = false;
            // if (currentStatus == SHOW_TOOLS &&
            // contentRichEditText.hasFocus()) {
            if (currentStatus == SHOW_TOOLS) {
                showSoftKeyBoard();
            }
        } else {
            flag = true;
            if (currentStatus == SHOW_KEY_BOARD) {
                showSoftKeyBoard();
            }
        }
    }

    /**
     * 反复切换系统软键盘
     */
    private void showSoftKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 工具栏添加图片的逻辑
     */
    public void gallery() {
        // 调用系统图库
        // Intent getImg = new Intent(Intent.ACTION_GET_CONTENT);
        // getImg.addCategory(Intent.CATEGORY_OPENABLE);
        // getImg.setType("image/*");
        Intent getImg = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getImg, 1001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001: {
                    // 添加图片
                    Bitmap originalBitmap = null;
                    Uri originalUri = data.getData();
                    originalBitmap = ImageLoader.getInstance().loadImageSync(
                            originalUri.toString());
                    if (originalBitmap != null) {
                        imgPath = "file://" + getAbsoluteImagePath(originalUri);
                        contentRichEditText.addImage(originalBitmap,
                                imgPath);
                    } else {
                        Toast.makeText(this, "获取图片失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 获取指定uri的本地绝对路径
     *
     * @param uri
     * @return
     */
    @SuppressWarnings("deprecation")
    protected String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
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


}
