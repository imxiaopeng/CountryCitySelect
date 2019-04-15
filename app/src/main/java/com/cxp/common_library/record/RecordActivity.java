package com.cxp.common_library.record;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cxp.common_library.R;
import com.cxp.common_library.record.utils.MediaManager;
import com.example.permission.PermissionUtils;
import com.yanzhenjie.permission.Permission;

import java.io.File;

public class RecordActivity extends AppCompatActivity {

    //播音控制器
    private MediaManager mMediaManager;
    //UI
    Button btn;
    private Handler mHandler = new Handler();
    private TextView tvTime;
    private RelativeLayout rlRecord;
    private RecorderUtils instance;
    private ImageView ivAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaManager.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void init() {
        tvTime = findViewById(R.id.tv_time);
        RelativeLayout rlPlay = (RelativeLayout) findViewById(R.id.rl_Play);
        ivAnim = (ImageView) findViewById(R.id.iv_anim);
        rlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
            }
        });
        instance = RecorderUtils.getInstance(new RecorderUtils.OnRecordListener() {
            @Override
            public void onStart() {
                Log.e("--", "开始录音");
            }

            @Override
            public void onProgress(int db) {
                Log.e("--", "录音中-分贝=" + db);
                long startTime = instance.getMp3Recorder().getStartTime();
                long currentTimeMillis = System.currentTimeMillis();
                long difTime = currentTimeMillis - startTime;
                if (difTime >= 60 * 1000) {
                    //最长60秒
                    instance.stop();
                    return;
                }
                final long seconds = difTime / 1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(seconds + "s");
                    }
                });
            }

            @Override
            public void onStop(File file) {
                long startTime = instance.getMp3Recorder().getStartTime();
                long endTime = instance.getMp3Recorder().getEndTime();
                Log.e("--", "停止录音");
            }
        });
        btn = findViewById(R.id.btn_record);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtils.newInstance(RecordActivity.this)
                        .checkPermissons(new PermissionUtils.OnPermissionGrantedListener() {
                            @Override
                            public void onPermissionGranted() {
                                startRec();
                            }
                        }, Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        mMediaManager = new MediaManager(this, mHandler);
    }

    /**
     * 播放录音
     */
    private void playRecord() {
        final AnimationDrawable drawable = (AnimationDrawable) ivAnim.getBackground();
        //播放网络，或本地
        mMediaManager.playSound(instance.getMp3Recorder().getRecordFilePath(), new MediaManager.OnPlayListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                drawable.stop();
            }

            @Override
            public void onPlay(MediaPlayer mp) {
                drawable.start();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void endRec() {
        instance.stop();
    }

    private void startRec() {
        instance.start();
    }

    public void stop(View view) {
        endRec();
    }
}
