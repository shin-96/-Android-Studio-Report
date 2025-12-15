package com.example.musicplayerlite;

public class MusicModel {
    private String title;
    private String artist;
    private String path;
    private long duration;
    private String albumArt;

    public MusicModel(String title, String artist, String path, long duration, String albumArt) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.albumArt = albumArt;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getPath() { return path; }
    public long getDuration() { return duration; }
    public String getAlbumArt() { return albumArt; }
}
