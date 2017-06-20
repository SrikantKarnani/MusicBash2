package com.example.android.musicbash;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.android.musicbash.Player.mp;

/**
 * Created by Srikant on 3/15/2017.
 */

public class PlayerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.start();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    void playMedia(){
//        updateSeekBar = new Thread() {
//            @Override
//            public void run() {
//                int total = mp.getDuration();
//                int current = 0;
//                sb.setMax(total);
//                while (current < total) {
//                    try {
//                        sleep(100);
//                        mHandler.post(updateUI);
//                        current = mp.getCurrentPosition();
//                        sb.setProgress(current);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Uri uri = Uri.parse(mySongs.get(position).getData());
//        mp = new MediaPlayer();
//        try {
//            mp.setDataSource(getApplicationContext(), uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mp.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        updateSeekBar.start();
//        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mHandler.post(updateUI);
//                mp.seekTo(seekBar.getProgress());
//            }
//        });

}
//    private final Runnable updateUI = new Runnable() {
//        public void run() {
//            try {
//                int duration = mp.getCurrentPosition();
//                String g = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toMinutes(duration) * 60);
//                tv1.setText(g);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };


}
