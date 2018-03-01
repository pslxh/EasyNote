package com.cmt.edittextimageview;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by admin on 2017/12/25.
 */

public class PlayActivity extends Activity {

    private String play_path;

    private MediaPlayer mediaPlayer = null;

    private String totalSize;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity);
        textView = (TextView) findViewById(R.id.play_progress);
        play_path = getIntent().getStringExtra("audio_path");
        if (TextUtils.isEmpty(play_path)) {
            finish();
            return;
        }
        initMedio();
    }

    private void initMedio() {
        try {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setDataSource(play_path);

            mediaPlayer.prepare();

            //设置循环播放
            mediaPlayer.setLooping(false);

            mediaPlayer.start();

            totalSize = convert(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    //加载下一首
                    handler.removeMessages(0);

                    finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(0);
    }

    private String convert(int duration) {
        //总秒
        int second = duration / 1000;
        //总分
        int minute = second / 60;
        //剩余秒数
        int miao = second % 60;
        return minute + ":" + miao;
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            totalSize = convert(mediaPlayer.getDuration());

            textView.setText(convert(mediaPlayer.getCurrentPosition()) + "/" + totalSize);

            handler.sendEmptyMessageDelayed(0, 1000);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
        }
    }
}
