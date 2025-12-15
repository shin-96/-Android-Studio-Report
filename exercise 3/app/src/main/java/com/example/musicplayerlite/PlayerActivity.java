package com.example.musicplayerlite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ArrayList<MusicModel> musicList;

    TextView txtTitle, txtArtist, txtCurrent, txtTotal;
    ImageView imgCover;
    SeekBar seek;
    ImageButton btnPrev, btnPlay, btnNext;

    MusicService musicService;
    boolean isBound = false;

    Handler handler = new Handler();

    private final BroadcastReceiver updateUiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(MusicService.ACTION_UI_UPDATE)) {
                updateUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        musicList = MainActivity.musicList;

        // Ánh xạ view
        txtTitle = findViewById(R.id.txtTitle);
        txtArtist = findViewById(R.id.txtArtist);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtTotal = findViewById(R.id.txtTotal);

        imgCover = findViewById(R.id.imgCover);
        seek = findViewById(R.id.seekBar);

        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);

        // Start and bind to service
        Intent intent = new Intent(this, MusicService.class);
        int pos = getIntent().getIntExtra("pos", -1);
        if (pos != -1) {
            intent.putExtra("pos", pos);
            startService(intent);
        }
        bindService(intent, connection, 0); // Bind without auto-creating


        // Nút Play / Pause
        btnPlay.setOnClickListener(v -> {
            if (musicService == null) return;
            if (musicService.isPlaying()) {
                musicService.pause();
            } else {
                musicService.play();
            }
        });

        // Next
        btnNext.setOnClickListener(v -> {
            if (musicService != null) {
                musicService.next();
            }
        });

        // Prev
        btnPrev.setOnClickListener(v -> {
            if (musicService != null) {
                musicService.prev();
            }
        });

        // Seekbar kéo
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser && musicService != null)
                    musicService.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });
    }

    // ======================= SERVICE CONNECT ============================
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MusicService.MusicBinder b = (MusicService.MusicBinder) binder;
            musicService = b.getService();
            isBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    // ======================= UPDATE UI =============================
    private void updateUI() {
        if(musicService == null || musicService.player == null) return;
        int p = MusicService.pos;
        MusicModel currentSong = musicList.get(p);

        txtTitle.setText(currentSong.getTitle());
        txtArtist.setText(currentSong.getArtist());

        btnPlay.setImageResource(musicService.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        // Load album art
        Glide.with(this)
                .load(Uri.parse(currentSong.getAlbumArt()))
                .placeholder(R.drawable.ic_music_default)
                .error(R.drawable.ic_music_default)
                .into(imgCover);
    }

    // Format mm:ss
    private String formatTime(int ms) {
        int sec = ms / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%d:%02d", min, sec);
    }

    // ======================= SEEK BAR REALTIME ======================
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicService != null && musicService.player != null) {
                try {
                    int duration = musicService.player.getDuration();
                    int current = musicService.player.getCurrentPosition();

                    seek.setMax(duration);
                    seek.setProgress(current);

                    txtCurrent.setText(formatTime(current));
                    txtTotal.setText(formatTime(duration));
                } catch (IllegalStateException e) {
                    // Player might not be initialized yet
                }
            }
            handler.postDelayed(this, 500);
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MusicService.ACTION_UI_UPDATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateUiReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateUiReceiver, filter);
        }
        handler.post(updateSeekBarRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateUiReceiver);
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
