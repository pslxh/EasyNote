package com.cmt.edittextimageview.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmt.edittextimageview.R;
import com.cmt.edittextimageview.util.Util;

/**
 * Created by 27371 on 2018/1/26.
 */

public class KeyDialog extends Dialog {
    private Context mContext;
    private TextView keyTip;
    private EditText keyEdit;
    private String firstKey;

    private String saveKeyWord;

    private boolean saveKey = false;

    public interface InputKeyCallBack {
        void inputKeySuccess();
    }
    private InputKeyCallBack callBack;

    public KeyDialog(@NonNull Context context, InputKeyCallBack callBack) {
        super(context);
        this.mContext = context;
        this.callBack = callBack;
    }
    public KeyDialog(@NonNull Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }
    protected KeyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveKeyWord = Util.get_keyword(mContext);
        View view = View.inflate(mContext, R.layout.key_dialog_layout, null);
        keyTip = (TextView) view.findViewById(R.id.key_tip);
        keyEdit = (EditText) view.findViewById(R.id.key_edit);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        if (saveKey = !TextUtils.isEmpty(saveKeyWord)) {
            keyTip.setText("请输入6位密码");
        } else {
            keyTip.setText("请设置6位密码");
        }
//        Window win = getWindow();
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.height = DensityUtil.dip2px(context, 250);
//        lp.width = DensityUtil.dip2px(context, 200);
//        win.setAttributes(lp);
        keyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = s.toString();
                Log.e("Adlog", "key:" + saveKey);
                if (!saveKey) {
                    if (TextUtils.isEmpty(firstKey)) {
                        if (key.length() == 6) {
                            firstKey = key;
                            keyEdit.setText("");
                            keyTip.setText("请再次确认密码");
                        }
                    } else {
                        if (key.length() == 6) {
                            if (key.equals(firstKey)) {
                                Util.save_keyword(mContext, key);
                                if (callBack != null)
                                    callBack.inputKeySuccess();
                                dismiss();
                            } else {
                                keyEdit.setText("");
                                Toast.makeText(mContext, "请再次确认密码", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (key.length() == 6) {
                        if (key.equals(saveKeyWord)) {
                            if (callBack != null)
                                callBack.inputKeySuccess();
                            dismiss();
                        } else {
                            keyTip.setText("密码错误！请再次确认");
                            keyEdit.setText("");
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();


    }
}
