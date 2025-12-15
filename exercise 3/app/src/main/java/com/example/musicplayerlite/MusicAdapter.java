package com.example.musicplayerlite;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MVH> {

    ArrayList<MusicModel> list;
    Context context;
    OnClick listener;

    public interface OnClick {
        void onClick(int pos);
    }

    public MusicAdapter(Context ctx, ArrayList<MusicModel> list, OnClick listener) {
        this.context = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVH(LayoutInflater.from(context)
                .inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVH h, int pos) {
        MusicModel m = list.get(pos);

        h.title.setText(m.getTitle());
        h.artist.setText(m.getArtist());
        h.duration.setText(format(m.getDuration()));

        // Clear old image and cancel any pending loads
        Glide.with(context).clear(h.imgCover);

        // Load album art with Glide
        Glide.with(context)
                .load(Uri.parse(m.getAlbumArt()))
                .placeholder(R.drawable.ic_music_default) // Default image
                .error(R.drawable.ic_music_default) // Image on error
                .into(h.imgCover);

        h.itemView.setOnClickListener(v -> listener.onClick(pos));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MVH extends RecyclerView.ViewHolder {
        TextView title, artist, duration;
        ImageView imgCover;

        public MVH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.txtTitle);
            artist = v.findViewById(R.id.txtArtist);
            duration = v.findViewById(R.id.txtDuration);
            imgCover = v.findViewById(R.id.imgCover);
        }
    }

    private String format(long ms) {
        if (ms <= 0) return "0:00";
        int s = (int) (ms / 1000);
        return String.format("%d:%02d", s / 60, s % 60);
    }
}
