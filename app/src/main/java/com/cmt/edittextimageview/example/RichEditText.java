package com.cmt.edittextimageview.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cmt.edittextimageview.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class RichEditText extends EditText {

    private Context mContext;

    private Editable mEditable;

    public RichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.mContext = context;

    }

    public RichEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;

    }

    /**
     * 添加一个图片
     *
     * @param bitmap
     * @param
     */
    public void addImage(Bitmap bitmap, String filePath) {
        Log.i("imgpath", filePath);
        String pathTag = "<img src=\"" + filePath + "\"/>";
        SpannableString spanString = new SpannableString(pathTag);
        SpannableString span = new SpannableString("\b\n");

        Editable edit_text = getEditableText();
        int index = getSelectionStart();
        edit_text.insert(index, span);

        // 获取屏幕的宽高
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int bmWidth = bitmap.getWidth();//图片高度
        int bmHeight = bitmap.getHeight();//图片宽度

        int zoomWidth = getWidth() - (paddingLeft + paddingRight);

        int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);

        Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
        ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
        spanString.setSpan(imgSpan, 0, pathTag.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spanString);
        } else {
            edit_text.insert(index, spanString);
        }
        index = getSelectionStart();
        edit_text.insert(index, span);
        setSpanClickable();

    }

    /**
     * @param bitmap
     * @param filePath
     * @param start
     * @param end
     */
    public void addImage(Bitmap bitmap, String filePath, int start, int end) {
        Log.i("imgpath", filePath);
        String pathTag = "<img src=\"" + filePath + "\"/>";
        SpannableString spanString = new SpannableString(pathTag);
        // 获取屏幕的宽高
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int bmWidth = bitmap.getWidth();//图片高度
        int bmHeight = bitmap.getHeight();//图片宽度
        int zoomWidth = getWidth() - (paddingLeft + paddingRight);
        int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
        Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
        ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
        spanString.setSpan(imgSpan, 0, pathTag.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable editable = this.getText(); // 获取edittext内容
        editable.delete(start, end);//删除
        editable.insert(start, spanString); // 设置spanString要添加的位置
        setSpanClickable();
    }
    /**
     * @param
     * @param filePath
     * @param start
     * @param end
     */
    public void addDefaultImage(String filePath, int start, int end) {
        Log.i("imgpath", filePath);
        String pathTag = "<img src=\"" + filePath + "\"/>";
        SpannableString spanString = new SpannableString(pathTag);
        // 获取屏幕的宽高
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int bmWidth = bitmap.getWidth();//图片高度
        int bmHeight = bitmap.getHeight();//图片宽度
        int zoomWidth = getWidth() - (paddingLeft + paddingRight);
        int zoomHeight = (int) (((float) zoomWidth / (float) bmWidth) * bmHeight);
        Bitmap newBitmap = zoomImage(bitmap, zoomWidth, zoomHeight);
        ImageSpan imgSpan = new ImageSpan(mContext, newBitmap);
        spanString.setSpan(imgSpan, 0, pathTag.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (mEditable == null) {
            mEditable = this.getText(); // 获取edittext内容
        }
        mEditable.delete(start, end);//删除
        mEditable.insert(start, spanString); // 设置spanString要添加的位置

    }

    /**
     * 对图片进行缩放
     *
     * @param bgimage
     * @param newWidth
     * @param newHeight
     * @return
     */
    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        //如果宽度为0 保持原图
        if (newWidth == 0) {
            newWidth = width;
            newHeight = height;
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public void setRichText(String text) {
        this.setText("");
        this.mEditable = this.getText();
        this.mEditable.append(text);
        //遍历查找
        String str = "<img.*?>";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {

            String matchString = matcher.group();

            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(matchString);

            while (m.find()) {

                Log.e("Alog", "数据：" + m.group(1));

                final String localFilePath = m.group(1);

                final int matchStringStartIndex = text.indexOf(matchString);

                final int matchStringEndIndex = matchStringStartIndex + matchString.length();

                Log.e("Adlog", "localFilePath:" + localFilePath);

                ImageLoader.getInstance().loadImage(localFilePath, getDisplayImageOptions(), new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String uri, View arg1) {
                        // TODO Auto-generated method stub
                        //插入一张默认图片
                        addDefaultImage(localFilePath, matchStringStartIndex, matchStringEndIndex);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingComplete(String uri, View arg1, Bitmap bitmap) {
                        // TODO Auto-generated method stub
                        addImage(bitmap, uri, matchStringStartIndex, matchStringEndIndex);
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }
        this.setText(mEditable);

       // this.requestFocus();

        this.setSelection(mEditable.length());
    }

    private DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.sun)
                .showImageForEmptyUri(R.drawable.sun)
                .showImageOnFail(R.drawable.sun)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).build();
        return options;
    }

    public String getRichText() {
        return this.getText().toString();
    }


    private void viewPicture(Bitmap bitmap) {
        if (bitmap == null)
            return;
        //将由图片生成Uri
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, null, null));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        mContext.startActivity(intent);
    }


    public void setSpanClickable() {
        //此方法比较靠谱
        final Spanned s = this.getText();
        //setMovementMethod很重要，不然ClickableSpan无法获取点击事件。
        this.setMovementMethod(LinkMovementMethod.getInstance());
        ImageSpan[] imageSpans = s.getSpans(0, s.length(), ImageSpan.class);
        for (final ImageSpan span : imageSpans) {
            final String image_src = span.getSource();
            final int start = s.getSpanStart(span);
            final int end = s.getSpanEnd(span);
            Log.i("Adlog", "setSpanClickable , image_src = " + image_src + " , start = " + start + " , end = " + end);
            ClickableSpan click_span = new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                    Bitmap bitmap = ((BitmapDrawable) span.getDrawable()).getBitmap();

                    viewPicture(bitmap);

                }
            };
            OnLongClickListener  listener=new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                  Log.e("Adlog","long click");

                    return false;
                }
            };

            ClickableSpan[] click_spans = s.getSpans(start, end,
                    ClickableSpan.class);
            if (click_spans.length != 0) {
                // remove all click spans
                for (ClickableSpan c_span : click_spans) {
                    ((Spannable) s).removeSpan(c_span);
                }
            }
            ((Spannable) s).setSpan(click_span, start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Log.i("Adlog", "length = " + s.getSpans(start, end, ClickableSpan.class).length);

        }
    }


}
