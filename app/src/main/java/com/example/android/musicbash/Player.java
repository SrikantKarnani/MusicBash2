package com.example.android.musicbash;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.duration;
import static android.R.attr.id;
import static android.R.attr.mode;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.musicbash.R.id.next;
import static com.example.android.musicbash.R.id.playerToolbar;

public class Player extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    SeekBar sb;
    int position;
    Toolbar playerToolbar;
    Boolean mode;

    Thread updateSeekBar = new Thread() {
        @Override
        public void run() {
            int total = mp.getDuration();
            int current = 0;
            while (current < total) {
                try {
                    sleep(500);
                    mHandler.post(updateUI);
                    current = mp.getCurrentPosition();
                    sb.setProgress(current);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    TextView tv1, tv2;
    ImageButton btPlay, btPrevious, btNext, btfastB, btfastF;
    MediaPlayer.OnCompletionListener next = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playN();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        tv1 = (TextView) findViewById(R.id.timeSpent);
        tv2 = (TextView) findViewById(R.id.leftTime);
        btPlay = (ImageButton) findViewById(R.id.play);
        btPrevious = (ImageButton) findViewById(R.id.previous);
        btNext = (ImageButton) findViewById(R.id.next);
        btfastB = (ImageButton) findViewById(R.id.fastB);
        btfastF = (ImageButton) findViewById(R.id.fastF);
        sb = (SeekBar) findViewById(R.id.seekBar);
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        playerToolbar = (Toolbar) findViewById(R.id.playerToolbar);
        btPlay.setOnClickListener(this);
        btPrevious.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btfastF.setOnClickListener(this);
        btfastB.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songList");
        position = b.getInt("pos", 0);
        mode = b.getBoolean("shuffle", false);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), uri);
        sb.setMax(mp.getDuration());
        updateSeekBar.start();
        play();
        tv2.setText(updateTime(mp));
        playerToolbar.setTitle(mySongs.get(position).getName());
        mp.setOnCompletionListener(next);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.post(updateUI);
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play:
                if (mp.isPlaying()) {
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.fastF:
                fastF();
                break;
            case R.id.fastB:
                fastB();
                break;
            case R.id.next:
                playN();
                break;
            case R.id.previous:
                playP();
                break;
        }
    }

    public void play() {
            try {
                btPlay.setImageResource(R.drawable.pause);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void pause() {
        btPlay.setImageResource(R.drawable.playbutton);
        mp.pause();
    }

    public void fastF() {
        mp.seekTo(mp.getCurrentPosition() + 5000);
    }

    public void fastB() {
        mp.seekTo(mp.getCurrentPosition() - 5000);
    }

    public void playN() {
        mp.stop();
        mp.release();
        if (mode) {
            Random r = new Random();
            position = r.nextInt(mySongs.size()) + 1;
        } else {
            position = (position + 1) % mySongs.size();
        }
        Uri uri2 = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), uri2);
        sb.setMax(mp.getDuration());
        btPlay.setImageResource(R.drawable.pause);
        tv2.setText(updateTime(mp));
        playerToolbar.setTitle(mySongs.get(position).getName());
        mp.start();
        mp.setOnCompletionListener(next);
    }

    public void playP() {
        try {
            mp.stop();
            mp.release();
            position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
            Uri uri1 = Uri.parse(mySongs.get(position).toString());
            mp = MediaPlayer.create(getApplicationContext(), uri1);
            sb.setMax(mp.getDuration());
            mp.start();
            btPlay.setImageResource(R.drawable.pause);
            tv2.setText(updateTime(mp));
            playerToolbar.setTitle(mySongs.get(position).getName());
            mp.setOnCompletionListener(next);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String updateTime(MediaPlayer mp) {
        int duration = mp.getDuration();
        String g = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toMinutes(duration) * 60);
        return g;
    }

    private final Runnable updateUI = new Runnable() {
        public void run() {
            try {
                int duration = mp.getCurrentPosition();
                String g = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toMinutes(duration) * 60);
                tv1.setText(g);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler mHandler = new Handler();
}