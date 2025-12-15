package com.example.musicplayerlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    public static ArrayList<MusicModel> musicList;

    private MusicService musicService;
    private boolean isBound = false;

    private RelativeLayout miniPlayerContainer;
    private TextView miniTxtTitle, miniTxtArtist;
    private ImageView miniImgCover;
    private ImageButton miniBtnPlay;

    private final BroadcastReceiver updateUiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(MusicService.ACTION_UI_UPDATE)) {
                updateMiniPlayer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rvMusic);
        miniPlayerContainer = findViewById(R.id.mini_player_container);
        miniTxtTitle = findViewById(R.id.mini_txtTitle);
        miniTxtArtist = findViewById(R.id.mini_txtArtist);
        miniImgCover = findViewById(R.id.mini_imgCover);
        miniBtnPlay = findViewById(R.id.mini_btnPlay);

        askPermission();
        bindMusicService();

        miniPlayerContainer.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, PlayerActivity.class);
            // Không gửi "pos" để PlayerActivity chỉ cần kết nối và hiển thị UI
            // mà không khởi động lại bài hát.
            startActivity(i);
        });

        miniBtnPlay.setOnClickListener(v -> {
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                } else {
                    musicService.play();
                }
            }
        });
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, 100);
                return;
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        loadMusicList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusicList();
        }
    }

    private void loadMusicList() {
        musicList = MusicLoader.load(this);
        Log.d("DEBUG_MUSIC", "Songs loaded: " + musicList.size());

        MusicAdapter adapter = new MusicAdapter(this, musicList, pos -> {
            Intent i = new Intent(MainActivity.this, PlayerActivity.class);
            i.putExtra("pos", pos);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void bindMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private void updateMiniPlayer() {
        if (musicService != null && musicService.player != null) {
            miniPlayerContainer.setVisibility(View.VISIBLE);
            MusicModel currentSong = musicList.get(MusicService.pos);
            miniTxtTitle.setText(currentSong.getTitle());
            miniTxtArtist.setText(currentSong.getArtist());
            miniBtnPlay.setImageResource(musicService.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

            // Load album art for mini player
            Glide.with(this)
                    .load(Uri.parse(currentSong.getAlbumArt()))
                    .placeholder(R.drawable.ic_music_default)
                    .error(R.drawable.ic_music_default)
                    .into(miniImgCover);
        } else {
            miniPlayerContainer.setVisibility(View.GONE);
        }
    }

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
        updateMiniPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateUiReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
