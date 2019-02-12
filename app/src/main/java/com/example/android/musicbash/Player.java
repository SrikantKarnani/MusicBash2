package com.example.android.musicbash;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.example.android.musicbash.R.id.seekBar;

public class Player extends AppCompatActivity implements View.OnClickListener {
    static MediaPlayer mp;
    List<Songs> mySongs;
    SeekBar sb;
    int position;
    Toolbar playerToolbar;
    RelativeLayout rl;
    Boolean mode;
    Context context ;
    Thread updateSeekBar;
    Intent inte;
    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
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
        context = getApplicationContext();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mySongs = (List) intent.getSerializableExtra("songList");
        position = b.getInt("position");
        mode = b.getBoolean("shuffle", false);
        initViews();
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


        Uri uri = Uri.parse(mySongs.get(position).getData());
        mp = new MediaPlayer();
        try {
            mp.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startServiceMethod();
        updateSeekBar.start();
        updateImage(position);
        tv2.setText(updateTime(mp));
        mp.setOnCompletionListener(next);

    }


    private void initViews(){
        tv1 = (TextView) findViewById(R.id.timeSpent);
        tv2 = (TextView) findViewById(R.id.leftTime);
        btPlay = (ImageButton) findViewById(R.id.play);
        btPrevious = (ImageButton) findViewById(R.id.previous);
        btNext = (ImageButton) findViewById(R.id.next);
        btfastB = (ImageButton) findViewById(R.id.fastB);
        btfastF = (ImageButton) findViewById(R.id.fastF);
        rl = (RelativeLayout) findViewById(R.id.activity_player);
        sb = (SeekBar) findViewById(seekBar);
        playerToolbar = (Toolbar) findViewById(R.id.playerToolbar);
        btPlay.setOnClickListener(this);
        btPrevious.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btfastF.setOnClickListener(this);
        btfastB.setOnClickListener(this);
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
        playerToolbar.setTitle(mySongs.get(position).getTitle());
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
//        stopServiceMethod();
        mp.stop();
        mp.release();
        if (mode) {
            Random r = new Random();
            position = r.nextInt(mySongs.size()) + 1;
        } else {
            position = (position + 1) % mySongs.size();
        }
        updateImage(position);
        Uri uri2 = Uri.parse(mySongs.get(position).getData());
        mp = MediaPlayer.create(getApplicationContext(), uri2);
        sb.setMax(mp.getDuration());
        btPlay.setImageResource(R.drawable.pause);
        tv2.setText(updateTime(mp));
        playerToolbar.setTitle(mySongs.get(position).getTitle());
        // updateSeekBar.destroy();
//        updateSeekBar.start();
        mp.setOnCompletionListener(next);
        startServiceMethod();
    }

    public void playP() {
        try {
//            stopServiceMethod();
            mp.stop();
            mp.release();
            position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
            updateImage(position);
            Uri uri1 = Uri.parse(mySongs.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri1);
            sb.setMax(mp.getDuration());
            btPlay.setImageResource(R.drawable.pause);
            tv2.setText(updateTime(mp));
            playerToolbar.setTitle(mySongs.get(position).getTitle());
            mp.setOnCompletionListener(next);
            startServiceMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerBar(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDominantSwatch();
                Palette.Swatch sw = palette.getMutedSwatch();
                if (swatch == null || sw == null)
                    swatch = palette.getMutedSwatch();
                sw = palette.getDarkMutedSwatch();// Sometimes vibrant swatch is not available
                if (swatch != null || sw != null) {
                    // Set the background color of the player bar based on the swatch color
                    playerToolbar.setBackgroundColor(swatch.getRgb());
                    setStatusBarColor(sw.getRgb());
                    // Update the track's title with the proper title text color
                    tv1.setTextColor(swatch.getBodyTextColor());
                    tv2.setTextColor(swatch.getBodyTextColor());
                    // Update the artist name with the proper body text color
//                    mArtist.setTextColor(swatch.getBodyTextColor());
                }
            }
        });
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

    void startServiceMethod() {
        inte = new Intent(this, PlayerService.class);
        startService(inte);
    }

    public void setStatusBarColor(int color) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    void updateImage(int position) {
        long albumID = Integer.parseInt(mySongs.get(position).getAlbumId());
        Uri uriArt = ContentUris.withAppendedId(sArtworkUri, albumID);
        Picasso.get().load(uriArt).error(R.drawable.dj).transform(new BlurTransformation(context)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                updatePlayerBar(bitmap);
                rl.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                rl.setBackground(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}