package com.example.musicplayerlite;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.ArrayList;

public class MusicService extends Service {

    MediaPlayer player;
    ArrayList<MusicModel> list;
    public static int pos = 0;

    public static final String CHANNEL_ID = "MusicPlayerLiteChannel";
    public static final String ACTION_UI_UPDATE = "action_ui_update";

    private MediaSessionCompat mediaSession;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Create and initialize the MediaSession
        mediaSession = new MediaSessionCompat(this, "MusicPlayerLite");

        // Set the callback to handle media button events
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                next();
            }

            @Override
            public void onSkipToPrevious() {
                prev();
            }

            @Override
            public void onSeekTo(long newPosition) {
                seekTo((int) newPosition);
            }
        });

        // Set the session as active
        mediaSession.setActive(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This is the standard way to handle media button events.
        // It delegates the action to the MediaSession.Callback.
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            MediaButtonReceiver.handleIntent(mediaSession, intent);
            return START_STICKY;
        }

        // This is for starting playback from the Activity
        if (intent != null && intent.hasExtra("pos")) {
            list = MainActivity.musicList;
            if (list != null && !list.isEmpty()) {
                pos = intent.getIntExtra("pos", 0);
                playSong();
            }
        }

        return START_NOT_STICKY;
    }

    private void playSong() {
        if (player != null) {
            player.stop();
            player.release();
        }

        if (list == null || list.isEmpty()) return;

        player = MediaPlayer.create(this, Uri.parse(list.get(pos).getPath()));
        player.setOnCompletionListener(mp -> next());
        player.start();

        // Update notification and broadcast UI change
        showNotification();
        sendUpdateBroadcast();
    }

    public void next() {
        if (list != null && !list.isEmpty()) {
            pos = (pos + 1) % list.size();
            playSong();
        }
    }

    public void prev() {
        if (list != null && !list.isEmpty()) {
            pos = (pos - 1 + list.size()) % list.size();
            playSong();
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            showNotification();
            sendUpdateBroadcast();
        }
    }

    public void play() {
        if (player != null && !player.isPlaying()) {
            player.start();
            showNotification();
            sendUpdateBroadcast();
        }
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void seekTo(int ms) {
        if (player != null) {
            player.seekTo(ms);
            // After seeking, we must update the playback state so the notification seekbar updates
            updatePlaybackState();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Lite",
                    NotificationManager.IMPORTANCE_LOW // Use LOW to prevent sound on notification update
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // This function tells the system about the player's state and capabilities
    private void updatePlaybackState() {
        if (player == null) return;

        // Describe the available actions
        long capabilities =
                PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SEEK_TO; // Important: Allow seeking

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(capabilities);

        int state = isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        
        long currentPosition = 0;
        try {
            currentPosition = player.getCurrentPosition();
        } catch (IllegalStateException e) {
            // Can happen if the player is not ready
        }

        // Set the state, position, and playback speed
        stateBuilder.setState(state, currentPosition, 1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    // This function tells the system about the current song
    private void updateMediaMetadata() {
        if (list == null || list.isEmpty() || player == null) return;

        MusicModel currentSong = list.get(pos);
        long duration = 0;
        try {
            duration = player.getDuration();
        } catch (IllegalStateException e) {
             // Can happen if the player is not ready
        }

        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, currentSong.getAlbumArt())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration) // Important: Provide duration for seekbar
                .build());
    }

    @SuppressLint("ForegroundServiceType")
    private void showNotification() {
        // Update the session state first so the notification is built with correct info
        updatePlaybackState();
        updateMediaMetadata();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        int playPauseIcon = isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;

        builder
                // The MediaStyle will use the MediaSession's metadata for title, artist, and album art.
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        // The expanded view will show up to 5 actions. The compact view (the "bigger" one) will show up to 3.
                        // This makes Previous, Play/Pause, and Next visible in the compact view.
                        .setShowActionsInCompactView(0, 1, 2))
                .setSmallIcon(R.drawable.ic_music_default)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Make visible on lock screen
                .setOnlyAlertOnce(true)
                // Use MediaButtonReceiver to build pending intents. This is the standard way.
                .addAction(R.drawable.ic_prev, "Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                .addAction(playPauseIcon, "Play/Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE))
                .addAction(R.drawable.ic_next, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        startForeground(1, builder.build());
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent(ACTION_UI_UPDATE);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
        stopForeground(true);
    }
}
