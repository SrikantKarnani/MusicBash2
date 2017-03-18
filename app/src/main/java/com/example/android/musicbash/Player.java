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
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.duration;
import static android.R.attr.id;
import static android.R.attr.mode;
import static android.R.attr.resource;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.musicbash.R.id.next;
import static com.example.android.musicbash.R.id.playerToolbar;
import static com.example.android.musicbash.R.id.seekBar;

public class Player extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    List<songs> mySongs;
    SeekBar sb;
    int position;
    Toolbar playerToolbar;
    RelativeLayout rl;
    Boolean mode;
    Context context;
    Thread updateSeekBar;
    Intent inte;
    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    CircleImageView im;
    TextView tv1, tv2;
    Handler mHandler;
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
        mySongs = new ArrayList<>();
        setContentView(R.layout.activity_player);
        tv1 = (TextView) findViewById(R.id.timeSpent);
        tv2 = (TextView) findViewById(R.id.leftTime);
        btPlay = (ImageButton) findViewById(R.id.play);
        btPrevious = (ImageButton) findViewById(R.id.previous);
        btNext = (ImageButton) findViewById(R.id.next);
        btfastB = (ImageButton) findViewById(R.id.fastB);
        btfastF = (ImageButton) findViewById(R.id.fastF);
        rl = (RelativeLayout) findViewById(R.id.activity_player);
        sb = (SeekBar) findViewById(seekBar);
        mHandler = new Handler();
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int total = mp.getDuration();
                int current = 0;
                sb.setMax(total);
                while (current < total) {
                    try {
                        sleep(100);
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
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        }
            im = (CircleImageView) findViewById(R.id.thumb);
            context = getApplicationContext();
            playerToolbar = (Toolbar) findViewById(R.id.playerToolbar);
            btPlay.setOnClickListener(this);
            btPrevious.setOnClickListener(this);
            btNext.setOnClickListener(this);
            btfastF.setOnClickListener(this);
            btfastB.setOnClickListener(this);
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            mySongs = (List) intent.getSerializableExtra("songList");
            position = b.getInt("position") ;
            if (position == -1) {
                position = mySongs.size() - 1;
            }
            mode = b.getBoolean("shuffle", false);
            Uri uri = Uri.parse(mySongs.get(position).getData());
            mp = new MediaPlayer();
            try {
                mp.setDataSource(getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mp.start();
        startServiceMethod(position);
        updateSeekBar.start();

        long albumID = Integer.parseInt(mySongs.get(position).getAlbumId());
            Uri uriArt = ContentUris.withAppendedId(sArtworkUri, albumID);
            Glide.with(context).load(uriArt).error(R.mipmap.music)
                    .crossFade().centerCrop().into(im);

            Glide.with(context).load(uriArt).error(R.mipmap.music).bitmapTransform(new BlurTransformation(context))
                    .crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    Drawable drawable = resource;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rl.setBackground(drawable);
                    }
                }
            });
            tv2.setText(updateTime(mp));
            playerToolbar.setTitle(mySongs.get(position).getTitle());
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
        long  albumID = Integer.parseInt(mySongs.get(position).getAlbumId());
        Uri uriArt = ContentUris.withAppendedId(sArtworkUri, albumID);
        Glide.with(context).load(uriArt).error(R.mipmap.music)
                .crossFade().centerCrop().into(im);
        Glide.with(context).load(uriArt).error(R.mipmap.music).bitmapTransform(new BlurTransformation(context))
                .crossFade().into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource,
                                        GlideAnimation<? super GlideDrawable> glideAnimation) {
                Drawable drawable = resource;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rl.setBackground(drawable);
                }
            }
        });
        startServiceMethod(position);
        Uri uri2 = Uri.parse(mySongs.get(position).getData());
        mp = MediaPlayer.create(getApplicationContext(), uri2);
        sb.setMax(mp.getDuration());
        btPlay.setImageResource(R.drawable.pause);
        tv2.setText(updateTime(mp));
        playerToolbar.setTitle(mySongs.get(position).getTitle());
//        mp.start();
        updateSeekBar.start();
        mp.setOnCompletionListener(next);
    }

    public void playP() {
        try {
            mp.stop();
            mp.release();
            position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
            long  albumID = Integer.parseInt(mySongs.get(position).getAlbumId());
            Uri uriArt = ContentUris.withAppendedId(sArtworkUri, albumID);
            Glide.with(context).load(uriArt).error(R.mipmap.music)
                    .crossFade().centerCrop().into(im);
            Glide.with(context).load(uriArt).error(R.mipmap.music).bitmapTransform(new BlurTransformation(context))
                    .crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource,
                                            GlideAnimation<? super GlideDrawable> glideAnimation) {
                    Drawable drawable = resource;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rl.setBackground(drawable);
                    }
                }
            });
            startServiceMethod(position);
            Uri uri1 = Uri.parse(mySongs.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri1);
            sb.setMax(mp.getDuration());
            updateSeekBar.start();
//            mp.start();
            btPlay.setImageResource(R.drawable.pause);
            tv2.setText(updateTime(mp));
            playerToolbar.setTitle(mySongs.get(position).getTitle());
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
    void startServiceMethod(int pos){
        inte = new Intent(this,PlayerService.class);
        startService(inte);
    }
}