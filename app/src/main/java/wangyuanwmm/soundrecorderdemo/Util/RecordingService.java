package wangyuanwmm.soundrecorderdemo.Util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import wangyuanwmm.soundrecorderdemo.R;
import wangyuanwmm.soundrecorderdemo.Util.DBHelper;
import wangyuanwmm.soundrecorderdemo.activities.MainActivity;

/**
 * 录音服务
 * Created by Administrator on 2017/5/3.
 */


public class RecordingService extends Service{

    private static final String TAG = "RecordService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private DBHelper mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
//    private static final SimpleDateFormat mTimerFormat =
//            new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    //开始录音
    public void startRecording() {
        SetFileNameAndPath();

        mRecorder = new MediaRecorder();
        //设置录制的音频来源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频输出格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置文件输出路径
        mRecorder.setOutputFile(mFilePath);
        //设置录制的编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置录制的音频通道数
        mRecorder.setAudioChannels(1);

        //startTimer();
        //startForeground(1, createNotification());

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(TAG, "startRecording: " + e);
        }
    }

    //设置文件名和路径
    private void SetFileNameAndPath() {
        int count = 0;
        File f;

        do {
            count++;
            mFileName = getString(R.string.default_file_name)
                    + " #" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;

            f = new File(mFilePath);
        } while (f.exists() && !f.isDirectory());
    }

    //停止录音
    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;

        try {
            //添加录音数据
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);
        } catch (Exception e){
            Log.e(TAG, "exception", e);
        }
    }

//    private void startTimer() {
//        mTimer = new Timer();
//        mIncrementTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                mElapsedSeconds++;
//                if (onTimerChangedListener != null)
//                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
//                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mgr.notify(1, createNotification());
//            }
//        };
//        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
//    }
//
//    //TODO:
//    private Notification createNotification() {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_mic_white_36dp)
//                        .setContentTitle(getString(R.string.notification_recording))
//                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
//                        .setOngoing(true);
//
//        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
//                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));
//
//        return mBuilder.build();
//    }
}
